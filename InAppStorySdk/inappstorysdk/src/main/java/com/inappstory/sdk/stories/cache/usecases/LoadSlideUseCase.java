package com.inappstory.sdk.stories.cache.usecases;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.SlideTask;
import com.inappstory.sdk.stories.cache.UrlWithAlter;


public class LoadSlideUseCase {
    private final IASCore core;
    private final SlideTask slideTask;


    public LoadSlideUseCase(SlideTask slideTask, IASCore core) {
        this.core = core;
        this.slideTask = slideTask;
    }

    private boolean downloadVODFile(
            String url,
            String uniqueKey,
            long start,
            long end
    ) {
        return new StoryVODResourceFileUseCase(
                core,
                url,
                uniqueKey,
                start,
                end
        ).getFile() != null;
    }

    private boolean downloadStaticFile(String url) {
        try {
            GetCacheFileUseCase<DownloadFileState> useCase =
                    new StoryResourceFileUseCase(
                            core,
                            url
                    );
            DownloadFileState state = useCase.getFile();
            return (state != null && state.getFullFile() != null);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean downloadFileWithAlter(String url, String alter) {
        boolean downloadResult = downloadStaticFile(url);
        if (!downloadResult && alter != null) downloadResult = downloadStaticFile(alter);
        return downloadResult;
    }

    public boolean loadWithResult() throws Exception {
        for (IResource object : slideTask.vodResources()) {
            long rangeStart = object.getRangeStart();
            long rangeEnd = object.getRangeEnd();
            if (!
                    downloadVODFile(
                            object.getUrl(),
                            object.getFileName(),
                            rangeStart,
                            rangeEnd
                    )
            )
                return false;
        }
        for (IResource object : slideTask.staticResources()) {
            if (!
                    downloadStaticFile(object.getUrl())
            )
                return false;
        }
        for (UrlWithAlter urlWithAlter : slideTask.urlsWithAlter()) {
            if (!
                    downloadFileWithAlter(urlWithAlter.getUrl(), urlWithAlter.getAlter())
            )
                return false;
        }
        return true;
    }
}
