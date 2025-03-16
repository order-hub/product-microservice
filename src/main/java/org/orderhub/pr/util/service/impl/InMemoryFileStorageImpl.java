package org.orderhub.pr.util.service.impl;

import org.orderhub.pr.util.dto.InMemoryFile;
import org.orderhub.pr.util.service.InMemoryFileStorage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFileStorageImpl implements InMemoryFileStorage {
    private final Map<String, InMemoryFile> storage = new ConcurrentHashMap<>();

    @Override
    public void put(String key, InMemoryFile file) {
        storage.put(key, file);
    }

    @Override
    public InMemoryFile get(String key) {
        return storage.get(key);
    }

    @Override
    public void remove(String key) {
        storage.remove(key);
    }
}
