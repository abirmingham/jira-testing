package com.extrahop.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.extrahop.api.ExVersion;
import com.extrahop.api.MyPluginComponent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExportAsService ({MyPluginComponent.class})
@Named ("myPluginComponent")
public class MyPluginComponentImpl implements MyPluginComponent
{
    @ComponentImport
    private final ApplicationProperties applicationProperties;

    private final int maxLookback = 500;

    @Inject
    public MyPluginComponentImpl(final ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public String getName()
    {
        if(null != applicationProperties)
        {
            return "myComponent:" + applicationProperties.getDisplayName();
        }

        return "myComponent";
    }

    public List<ExVersion> findVersions(String reference) throws IOException {
        List<ExVersion> versions = new ArrayList<ExVersion>();

        // now open the resulting repository with a FileRepositoryBuilder
        File repoDir = Paths.get(
            System.getProperty("user.home"),
            "Documents",
            "depot-mimic",
            ".git"
        ).toFile();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(repoDir)
            .findGitDir() // scan up the file system tree
            .build();
        RevWalk walk = new RevWalk( repo );
        System.out.println("Having repository: " + repo.getDirectory());
        // TODO pull

        RevCommit commit = walk.parseCommit(repo.resolve(reference));

        for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
            if (e.getValue().getName().startsWith("refs/heads/release/")) {
                RevCommit releaseHead = walk.parseCommit(e.getValue().getObjectId());

                if (walk.isMergedInto(commit, releaseHead)) {
                    RevCommit currentCommit = releaseHead.getParent(0);
                    RevCommit goal = null;
                    int count = 0;

                    while (goal == null && currentCommit != null && count < this.maxLookback) {
                        if (!walk.isMergedInto(commit, currentCommit.getParent(0))) {
                            goal = currentCommit;
                        }
                        else {
                            currentCommit = currentCommit.getParent(0);
                        }
                        count++;
                    }

                    // TODO run revision.sh instead of this crap
                    String branchName = e.getValue().getName().substring("refs/heads/".length());
                    String version;
                    if (goal != null) {
                        version = branchName + '^' + count;
                    }
                    else {
                        version = branchName + '^' + (count  + 1) + " (or before)";
                    }
                    versions.add(new ExVersion(branchName, version));
                }
            }
        }

        return versions;
    }
}