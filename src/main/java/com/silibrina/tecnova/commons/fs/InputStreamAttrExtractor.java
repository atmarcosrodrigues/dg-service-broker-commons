package com.silibrina.tecnova.commons.fs;

import com.silibrina.tecnova.commons.exceptions.UnrecoverableErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import static com.silibrina.tecnova.commons.exceptions.ExitStatus.DEFAULT_ERROR_STATUS;
import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getInputStream;

public class InputStreamAttrExtractor {
    private static final Logger logger = LoggerFactory.getLogger(InputStreamAttrExtractor.class);

    private static final int BLOCK_SIZE = 4096;
    private static final String MD5_ALGORITHM = "MD5";

    public static InputStreamAttr extractAttr(File file) throws IOException {
        try (InputStream inputStream = getInputStream(file.toURI())) {
            return extractAttr(inputStream);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static InputStreamAttr extractAttr(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BLOCK_SIZE];
        MessageDigest md = getHashAlgorithm();
        DigestInputStream dis = new DigestInputStream(inputStream, md);
        long count = 0;

        while ((count += dis.read(buffer)) != -1) ;

        return new InputStreamAttr(DatatypeConverter.printHexBinary(md.digest()).toLowerCase(), count);
    }

    private static MessageDigest getHashAlgorithm() {
        try {
            return MessageDigest.getInstance(MD5_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            logger.error("hash algorithm not found", e);
            throw new UnrecoverableErrorException("hash algorithm not found", DEFAULT_ERROR_STATUS);
        }
    }

    public static class InputStreamAttr {
        public final String checksum;
        public final long size;

        public InputStreamAttr(String checksum, long size) {
            this.checksum = checksum;
            this.size = size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InputStreamAttr that = (InputStreamAttr) o;

            if (size != that.size) return false;
            return checksum != null ? checksum.equals(that.checksum) : that.checksum == null;
        }

        @Override
        public int hashCode() {
            int result = checksum != null ? checksum.hashCode() : 0;
            result = 31 * result + (int) (size ^ (size >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(),
                    "%s [checksum: %s, size: %d]",
                    this.getClass().getSimpleName(), checksum, size);
        }
    }
}