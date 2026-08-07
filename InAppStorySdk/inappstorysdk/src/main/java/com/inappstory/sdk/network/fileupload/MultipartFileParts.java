package com.inappstory.sdk.network.fileupload;

import java.util.List;

public class MultipartFileParts {
    private final List<FilePart> fileParts;

    public MultipartFileParts(List<FilePart> fileParts) {
        this.fileParts = fileParts;
    }
}
