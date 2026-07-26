package jb.common.sound;

import jb.common.ExceptionReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MediaConcatenator
{
    private final static Logger logger = LogManager.getLogger(MediaConcatenator.class);

    private static final AudioFormat TARGET_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            44100f, 16, 2, 4, 44100f, false);

    public static void concat(List<URL> urlList, String outputFile, File tempDir) {
        logger.info("Concatenating {} items", urlList.size());

        List<AudioInputStream> opened = new ArrayList<>();
        try {
            List<AudioInputStream> normalized = new ArrayList<>();
            long totalFrames = 0;

            for (URL u : urlList) {
                logger.debug("Opening: {}", u);
                AudioInputStream raw = AudioSystem.getAudioInputStream(u);
                opened.add(raw);

                AudioInputStream pcm = toTargetFormat(raw);
                opened.add(pcm);
                normalized.add(pcm);

                long frames = pcm.getFrameLength();
                totalFrames = (frames == AudioSystem.NOT_SPECIFIED || totalFrames == AudioSystem.NOT_SPECIFIED)
                        ? AudioSystem.NOT_SPECIFIED
                        : totalFrames + frames;
            }

            InputStream sequenced = normalized.get(0);
            for (int i = 1; i < normalized.size(); i++) {
                sequenced = new SequenceInputStream(sequenced, normalized.get(i));
            }

            AudioInputStream combined = new AudioInputStream(sequenced, TARGET_FORMAT, totalFrames);
            File out = new File(outputFile);
            AudioSystem.write(combined, AudioFileFormat.Type.WAVE, out);

            logger.info("Wrote {}", out.getAbsolutePath());
        } catch (UnsupportedAudioFileException | IOException e) {
            ExceptionReporter.reportException(e);
        } finally {
            for (AudioInputStream s : opened) {
                try { s.close(); } catch (IOException ignored) {}
            }
        }
    }

    private static AudioInputStream toTargetFormat(AudioInputStream source) {
        AudioFormat sourceFormat = source.getFormat();

        AudioFormat nativePcm = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);

        AudioInputStream pcmStream = AudioSystem.isConversionSupported(nativePcm, sourceFormat)
                ? AudioSystem.getAudioInputStream(nativePcm, source)
                : source;

        return AudioSystem.isConversionSupported(TARGET_FORMAT, pcmStream.getFormat())
                ? AudioSystem.getAudioInputStream(TARGET_FORMAT, pcmStream)
                : pcmStream;
    }
}
