package io.casestory.sdk.stories.api.models;

import java.util.ArrayList;

import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.stories.utils.KeyValueStorage;

public class BuilderSaveObject {

        public boolean sandbox = false;
        public String userId;
        public String apiKey;
        public String testKey;
        public ArrayList<String> tags;

        public BuilderSaveObject(CaseStoryManager.Builder builder) {
            this.sandbox = builder.sandbox();
            this.userId = builder.userId();
            this.apiKey = builder.apiKey();
            this.testKey = builder.testKey();
            this.tags = builder.tags();
        }

        public void save() {
            KeyValueStorage.saveObject("managerInstance", BuilderSaveObject.this);
        }
    }
