package com.extrahop.api;

import java.io.IOException;
import java.util.List;

public interface MyPluginComponent
{
    String getName();
    List<ExVersion> findVersions(String ref) throws IOException;
}