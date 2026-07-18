package com.ruoyi.common.utils.file;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import com.ruoyi.common.exception.file.InvalidExtensionException;

class FileUploadUtilsTest
{
    @Test
    void rejectsScriptDisguisedAsJpeg()
    {
        MockMultipartFile file = new MockMultipartFile("file", "attack.jpg", "image/jpeg",
                "<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8));

        assertThrows(InvalidExtensionException.InvalidImageExtensionException.class,
                () -> FileUploadUtils.assertAllowed(file, MimeTypeUtils.IMAGE_EXTENSION));
    }

    @Test
    void acceptsMatchingPngSignature()
    {
        byte[] pngHeader = new byte[] { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", pngHeader);

        assertDoesNotThrow(() -> FileUploadUtils.assertAllowed(file, MimeTypeUtils.IMAGE_EXTENSION));
    }

    @Test
    void genericUploadAlsoRejectsAFileDisguisedAsImage()
    {
        MockMultipartFile file = new MockMultipartFile("file", "attack.jpg", "image/jpeg",
                "not-a-real-image".getBytes(StandardCharsets.UTF_8));

        assertThrows(InvalidExtensionException.InvalidImageExtensionException.class,
                () -> FileUploadUtils.assertAllowed(file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION));
    }
}
