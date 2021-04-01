package pl.edu.pwr.ontologydatagenerator.infrastructure.configuration.marshalling;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class CdataEscapeHandler implements CharacterEscapeHandler {

    @Override
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        int limit = start + length;
        for (int i = start; i < limit; i++) {
            if (!isAttVal
                    && i <= limit - 12
                    && ch[i] == '<'
                    && ch[i + 1] == '!'
                    && ch[i + 2] == '['
                    && ch[i + 3] == 'C'
                    && ch[i + 4] == 'D'
                    && ch[i + 5] == 'A'
                    && ch[i + 6] == 'T'
                    && ch[i + 7] == 'A'
                    && ch[i + 8] == '[') {
                int cdataEnd = i + 8;
                for (int k = i + 9; k < limit - 2; k++) {
                    if (ch[k] == ']'
                            && ch[k + 1] == ']'
                            && ch[k + 2] == '>') {
                        cdataEnd = k + 2;
                        break;
                    }
                }
                out.write(ch, start, cdataEnd + 1);
                if (cdataEnd == limit - 1) return;
                start = i = cdataEnd + 1;
            }
            char c = ch[i];
            if (c == '&' || c == '<' || c == '>' || c == '\r' || (c == '\"' && isAttVal)) {
                if (i != start)
                    out.write(ch, start, i - start);
                start = i + 1;
                switch (ch[i]) {
                    case '&' -> out.write("&amp;");
                    case '<' -> out.write("&lt;");
                    case '>' -> out.write("&gt;");
                    case '\"' -> out.write("&quot;");
                }
            }
        }

        if (start != limit)
            out.write(ch, start, limit - start);
    }
}