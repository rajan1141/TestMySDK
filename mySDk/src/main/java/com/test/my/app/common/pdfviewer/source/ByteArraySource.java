package com.test.my.app.common.pdfviewer.source;
import android.content.Context;

import com.test.my.app.common.pdfviewer.pdfium.PdfDocument;
import com.test.my.app.common.pdfviewer.pdfium.PdfiumCore;

import java.io.IOException;

public class ByteArraySource implements DocumentSource {

    private byte[] data;

    public ByteArraySource(byte[] data) {
        this.data = data;
    }

    @Override
    public PdfDocument createDocument(Context context, PdfiumCore core, String password) throws IOException {
        return core.newDocument(data, password);
    }
}
