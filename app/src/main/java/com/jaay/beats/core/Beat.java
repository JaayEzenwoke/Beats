package com.jaay.beats.core;

import static com.jaay.beats.tools.Utils.getTrimmed;
import static com.jaay.beats.tools.Utils.getUniqueFilename;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.audiofx.Equalizer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.jaay.beats.tools.Utils;
import com.jaay.beats.types.Audio;

import java.io.File;


public class Beat {

    private static final String TAG = "Beat";

    private static final int BUFFER_SIZE = 16 * 1024; // 16KB buffer size

    private volatile boolean ispaused;

    private final MediaExtractor extractor;
    private final ArrayList<Audio> tracks;
    private MediaCodec decoder;
    private AudioTrack track;
    private Context context;

    public Beat(Context context) {
        this.context = context;
        this.extractor = new MediaExtractor();
        tracks = getSongs();
    }

    @SuppressLint("Range")
    private ArrayList<Audio> getSongs() {
        ArrayList<Audio> tracks = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATE_ADDED
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
                String artist     = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String title      = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String path       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                Audio audio = new Audio(artist, title, path);
                audio.setDate(date_added);
                tracks.add(audio);
            }
            cursor.close();
            Collections.sort(tracks, new Comparator<Audio>() {

                @Override
                public int compare(Audio track_1, Audio track_2) {
                    return track_1.getTitle().compareTo(track_2.getTitle());
                }
            });

            return tracks;
        } else {
            return tracks;
        }
    }

    public ArrayList<Audio> getTracks() {
        return tracks;
    }

    // Initialize AudioTrack with given sample rate and channel configuration
    private AudioTrack initialize(int rate, int configuration) {
        int min_buffer = AudioTrack.getMinBufferSize(rate, configuration, AudioFormat.ENCODING_PCM_16BIT);
        int buffer_size = Math.max(min_buffer, BUFFER_SIZE);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        AudioFormat format = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(rate)
                .setChannelMask(configuration)
                .build();

        return new AudioTrack(
                attributes,
                format,
                buffer_size,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
        );
    }
    

    // Play audio from the given file path
    public void play(String path) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {

            @Override
            public void run() {
                decodeAndPlay(path);
            }
        });
    }

//    // Decode and play audio simultaneously
//    private void decodeAndPlay(String path) {
//        try {
//            extractor.setDataSource(path);
//            int track_index = selectTrack();
//            if (track_index == -1) return;
//
//            MediaFormat format = extractor.getTrackFormat(track_index);
//            String mime = format.getString(MediaFormat.KEY_MIME);
//            int sample_rate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
//            int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
//            int configuration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
//
//            decoder = MediaCodec.createDecoderByType(mime);
//            decoder.configure(format, null, null, 0);
//            track = initialize(sample_rate, configuration);
//            decoder.start();
//
//            track.play();
//
//            ByteBuffer input;
//            ByteBuffer output;
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//            boolean isEOS = false;
//
//            while (!Thread.interrupted()) {
//                if (ispaused) {
//                    Thread.sleep(100); // Sleep to avoid busy waiting when paused
//                    continue;
//                }
//
//                if (!isEOS) {
//                    int inputBufferIndex = decoder.dequeueInputBuffer(10000);
//                    if (inputBufferIndex >= 0) {
//                        input = decoder.getInputBuffer(inputBufferIndex);
//                        int size = extractor.readSampleData(input, 0);
//
//                        if (size < 0) {
//                            decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                            isEOS = true;
//                        } else {
//                            decoder.queueInputBuffer(inputBufferIndex, 0, size, extractor.getSampleTime(), 0);
//                            extractor.advance();
//                        }
//                    }
//                }
//
//                int buffer = decoder.dequeueOutputBuffer(info, 10000);
//                if (buffer >= 0) {
//                    output = decoder.getOutputBuffer(buffer);
//                    byte[] chunk = new byte[info.size];
//                    output.get(chunk);
//                    output.clear();
//
//                    if (chunk.length > 0) {
//                        track.write(chunk, 0, chunk.length);
//                    }
//                    decoder.releaseOutputBuffer(buffer, false);
//
//                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                        break;
//                    }
//                }
//            }
//        } catch (IOException | InterruptedException exception) {
//            Log.e(TAG, "Error in decodeAndPlay: " + exception.getMessage());
//        } finally {
//            cleanUp(track, decoder, extractor);
//        }
//    }

    private void decodeAndPlay(String path) {
        try {
            extractor.setDataSource(path);
            int track_index = selectTrack();
            if (track_index == -1) return;

            MediaFormat format = extractor.getTrackFormat(track_index);
            String mime = format.getString(MediaFormat.KEY_MIME);
            int sample_rate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            int configuration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

            decoder = MediaCodec.createDecoderByType(mime);
            decoder.configure(format, null, null, 0);
            track = initialize(sample_rate, configuration);
            decoder.start();

            track.play();

            ByteBuffer input;
            ByteBuffer output;
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            boolean isEOS = false;

            // FFT setup
            int fftSize = 1024; // Choose a power of 2
            FFT fft = new FFT(fftSize);
            double[] real = new double[fftSize];
            double[] imag = new double[fftSize];

            while (!Thread.interrupted()) {
                if (ispaused) {
                    Thread.sleep(100);
                    continue;
                }

                if (!isEOS) {
                    int inputBufferIndex = decoder.dequeueInputBuffer(10000);
                    if (inputBufferIndex >= 0) {
                        input = decoder.getInputBuffer(inputBufferIndex);
                        int size = extractor.readSampleData(input, 0);

                        if (size < 0) {
                            decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isEOS = true;
                        } else {
                            decoder.queueInputBuffer(inputBufferIndex, 0, size, extractor.getSampleTime(), 0);
                            extractor.advance();
                        }
                    }
                }

                int buffer = decoder.dequeueOutputBuffer(info, 10000);
                if (buffer >= 0) {
                    output = decoder.getOutputBuffer(buffer);
                    byte[] chunk = new byte[info.size];
                    output.get(chunk);
                    output.clear();

                    if (chunk.length > 0) {
                        // Convert PCM bytes to floating-point samples
                        short[] pcmSamples = Utils.byteArrayToShortArray(chunk);
                        Utils.processWithFFT(pcmSamples, fft, real, imag);

                        // Convert back to bytes after processing
                        byte[] processedChunk = Utils.shortArrayToByteArray(pcmSamples);
                        track.write(processedChunk, 0, processedChunk.length);
                    }
                    decoder.releaseOutputBuffer(buffer, false);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        break;
                    }
                }
            }
        } catch (IOException | InterruptedException exception) {
            Log.e(TAG, "Error in decodeAndPlay: " + exception.getMessage());
        } finally {
            cleanUp(track, decoder, extractor);
        }
    }

    // Pause audio playback
    public void pause() {
        ispaused = true;
        if (track != null) {
            track.pause();
        }
    }

    // Resume audio playback
    public void play() {
        ispaused = false;
        if (track != null) {
            track.play();
        }
    }

    // Get the duration of the audio file in milliseconds
    public long getDuration(String filePath) {
        try {
            extractor.setDataSource(filePath);
            int audioTrackIndex = selectTrack();
            if (audioTrackIndex == -1) return 0;

            MediaFormat format = extractor.getTrackFormat(audioTrackIndex);
            long durationUs = format.getLong(MediaFormat.KEY_DURATION);
            extractor.release();

            return durationUs / 1000; // Convert to milliseconds
        } catch (IOException e) {
            Log.e(TAG, "Error in getDuration: " + e.getMessage());
            return 0;
        }
    }

    // Seek to a specific position in the audio
    public void seekTo(long point) {
        if (extractor != null) {
            long position = point * 1000;
            extractor.seekTo(position, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            if (track != null) {
                track.flush();
            }
            if (decoder != null) {
                decoder.flush();
            }
        }
    }

    // Select the audio track from the extractor
    private int selectTrack() {
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                extractor.selectTrack(i);
                return i;
            }
        }
        return -1;
    }

    // Cut and save a portion of the audio file
    public void cutAndSaveAudio(Audio audio, int start, int end) {
        String input = audio.getPath();
        File file = new File(input);
        
        String output = context.getExternalCacheDir().getAbsolutePath() + "/" + getUniqueFilename(audio, tracks);

        File input_file = new File(input);
        File output_file = new File(output);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            extractor.setDataSource(audio.getPath());
            int track_index = selectTrack();
            if (track_index == -1) return;

            MediaFormat format = extractor.getTrackFormat(track_index);
            int sample_rate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);

            fis = new FileInputStream(input_file);
            fos = new FileOutputStream(output_file);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            int startbyte = (start * sample_rate * 2) / 1000;
            int endbyte = (end * sample_rate * 2) / 1000;

            int currentbyte = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                if (currentbyte + bytesRead > startbyte && currentbyte < endbyte) {
                    int bufferStart = Math.max(startbyte - currentbyte, 0);
                    int bufferEnd = Math.min(endbyte - currentbyte, bytesRead);
                    fos.write(buffer, bufferStart, bufferEnd - bufferStart);
                }
                currentbyte += bytesRead;
            }

            String title = getTrimmed(output_file.getName());
            String path = output_file.getPath();
            String artist = audio.getArtist();

            Audio created = new Audio(artist, title, path);
            tracks.add(created);
        } catch (IOException exception) {
            Utils.debug("exception: " + exception.getMessage());
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Slow down audio by adjusting the sample rate
    private int slowDownAudio(int originalSampleRate, float slowDownFactor) {
        return (int)(originalSampleRate / slowDownFactor);
    }

    // Set up an equalizer for the audio session
    private Equalizer setupEqualizer(int session_id) {
        Equalizer equalizer = new Equalizer(0, session_id);
        equalizer.setEnabled(true);

        // Example: Boost bass
        short bands = equalizer.getNumberOfBands();
        equalizer.setBandLevel((short)0, (short)1000); // Boost the first band (usually bass)

        return equalizer;
    }

    // Clean up resources
    private void cleanUp(AudioTrack track, MediaCodec decoder, MediaExtractor extractor) {
        if (track != null) {
            track.stop();
            track.release();
        }
        if (decoder != null) {
            decoder.stop();
            decoder.release();
        }
        if (extractor != null) {
            extractor.release();
        }
    }
}
