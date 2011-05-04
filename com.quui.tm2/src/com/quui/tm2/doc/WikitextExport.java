package com.quui.tm2.doc;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

import com.quui.tm2.Experiment;
import com.quui.tm2.util.TM2Logger;
import com.quui.tm2.util.FileIO;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class WikitextExport {

    private static void of(Experiment x, String location, String gold) {
        ExperimentWikitextTemplate generator = new ExperimentWikitextTemplate();
        String resultFile = x.getOutputAnnotationLocation();
        TM2Logger.singleton().warn("Result file trying to use: " + resultFile);
        ExperimentInfo metadata = ExportHelper.createMetadata(x);
        ExportHelper.renderDot(metadata, x);
        String string = generator.generate(metadata);
        MarkupParser parser = new MarkupParser(new MediaWikiLanguage());
        String html = parser.parseToHtml(string);
        FileIO.write(html, location);
    }

    public static String of(Experiment x) {
        String target = x.getOutputDocumentationLocation();
        of(x, target, Preferences.get(Default.GOLD));
        return target;
    }
}
