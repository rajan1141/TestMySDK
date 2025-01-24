package com.test.my.app.common.pdfviewer.source;
import android.content.Context;

import com.test.my.app.common.pdfviewer.pdfium.PdfDocument;
import com.test.my.app.common.pdfviewer.pdfium.PdfiumCore;

import java.io.IOException;

public interface DocumentSource {
    PdfDocument createDocument(Context context, PdfiumCore core, String password) throws IOException;
}
