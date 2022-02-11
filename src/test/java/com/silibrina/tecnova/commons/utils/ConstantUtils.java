package com.silibrina.tecnova.commons.utils;

public class ConstantUtils {
    private static final String DIR = "test-files/";

    public enum FilesPathUtils {
        JSON_FILE_1(DIR + "file-1.json", "json", 839L, "94d54169c4754531975eabfd6e1d9b3d",
                null, null, null, "application/octet-stream"),
        TXT_FILE_1(DIR + "file-1.txt", "txt", 486L, "84aff190e12342fd1fd03d28ba421c5f",
                null, null, null, "null"),
        TXT_FILE_2(DIR + "file-2.txt", "txt", 36508L, "2bc516074d4f8e9fee1b68424d01cd79",
                null, null, null, "null"),
        TXT_FILE_3(DIR + "file-3.txt", "txt", 67L, "1bd4562f10b5f6442e9a6e8be8bcce1b",
                null, null, null, "null"),
        XML_FILE_1(DIR + "file-1.xml", "xml", 122L, "99f93fe9c3f25e3e1c31b68ddf7fd233",
                null, null, null, "null"),
        CSV_FILE_1(DIR + "file-1.csv", "csv", 568959L, "f8b142ec31a75df933caeed75c2f0495",
                null, null, null, "null"),
        XLS_FILE_1(DIR + "file-1.xls", "xls", 925696L, "f97c47a00070bc0b945268a26fc8c14a",
                null, null, null, "null"),
        XLSX_FILE_1(DIR + "file-1.xlsx", "xlsx", 17315L, "f27f05c84027563cb9319292ace48aad",
                null, null, null, "null"),
        DOC_FILE_1(DIR + "file-1.doc", "doc", 512000L, "8e98658aee1d90b81b313c90a3bc161f",
                null, null, null, "null"),
        ODT_FILE_1(DIR + "file-1.odt", "odt", 6737L, "d04c8796c727e1de7e1d9fffd8ea5655",
                null, null, null, "null"),
        PDF_FILE_1(DIR + "file-1.pdf", "pdf", 288316L, "1e89f7f938b997b57d13a0a1b2741703",
                "application/pdf", "pdf", "application", "null"),
        PDF_FILE_2(DIR + "file-2.pdf", "pdf", 19944L, "46de036610c5078d3689cc9e5d838177",
                "application/pdf", "pdf", "application", "null"),// octothorpe
        PNG_FILE_1(DIR + "file-1.png", "png", 169821L, "5afa634358da076899acf8ee4ec8140d",
                "application/png", "png", "application", "null");

        public final String path;
        public final String format;
        public final long size;
        public final String hash;
        public final String mimeType;
        public final String subType;
        public final String type;
        public final String contentType;

        FilesPathUtils(String path, String format, long size, String hash, String mimeType,
                       String subType, String type, String contentType) {
            this.path = path;
            this.format = format;
            this.size = size;
            this.hash = hash;
            this.mimeType = mimeType;
            this.subType = subType;
            this.type = type;
            this.contentType = contentType;
        }
    }
}
