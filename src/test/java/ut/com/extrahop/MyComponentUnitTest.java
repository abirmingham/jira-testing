package ut.com.extrahop;

import com.extrahop.api.ExVersion;
import org.junit.Test;
import com.extrahop.api.MyPluginComponent;
import com.extrahop.impl.MyPluginComponentImpl;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }

    @Test
    public void testGetVersionInfo() throws IOException {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        String ref = "53b1db223b47c5060e5dea9338eb780535f4ec53";
        List<ExVersion> versions = component.findVersions(ref);
        for (int i = 0; i < versions.size(); i++) {
            System.out.println("---");
            System.out.println("- Branch " + versions.get(i).getBranch());
            System.out.println("- Version " + versions.get(i).getVersion());
        }
        if (versions.size() == 0) {
            System.out.println("No branches contained ref " + ref);
        }
    }
}