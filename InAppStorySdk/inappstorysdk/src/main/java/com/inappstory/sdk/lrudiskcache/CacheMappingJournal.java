package com.inappstory.sdk.lrudiskcache;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CacheMappingJournal {
    private long currentSize;
    FileManager fileManager;

    public static final int VERSION = 1;
    private File journalFile;

    List<CacheMappingJournalItem> cacheMappingJournalItems = new ArrayList<>();

}
