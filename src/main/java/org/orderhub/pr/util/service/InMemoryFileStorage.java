package org.orderhub.pr.util.service;

import org.orderhub.pr.util.dto.InMemoryFile;

public interface InMemoryFileStorage {
    void put(String key, InMemoryFile file);
    InMemoryFile get(String key);
    void remove(String key);
}
