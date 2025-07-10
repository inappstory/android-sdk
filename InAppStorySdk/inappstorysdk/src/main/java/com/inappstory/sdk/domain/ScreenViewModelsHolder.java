package com.inappstory.sdk.domain;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenViewModelsHolder implements IScreenViewModelsHolder {
    private final IASCore core;
    private IIAMReaderViewModel iamReaderViewModel;
    private final Map<Integer, IIAMReaderViewModel> iamReaderViewModels;

    public ScreenViewModelsHolder(IASCore core) {
        this.core = core;
       // this.iamReaderViewModel = new IAMReaderViewModel(core);
        this.iamReaderViewModels = new HashMap<>();
    }

    @Override
    public IIAMReaderViewModel iamReaderViewModel() {
        if (iamReaderViewModel == null) iamReaderViewModel = new IAMReaderViewModel(core);
        return iamReaderViewModel;
    }

    @Override
    public IIAMReaderViewModel iamReaderViewModel(int id) {
        if (!iamReaderViewModels.containsKey(id)) {
            IAMReaderViewModel viewModel = new IAMReaderViewModel(core);
            iamReaderViewModels.put(id, viewModel);
            iamReaderViewModel = viewModel;
        }
        return iamReaderViewModels.get(id);
    }
}
