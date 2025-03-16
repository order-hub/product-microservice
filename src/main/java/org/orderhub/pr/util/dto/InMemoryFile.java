package org.orderhub.pr.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryFile {
    private String originalFilename;
    private String contentType;
    private byte[] content;

    public int getSize() {
        return content.length;
    }
}
