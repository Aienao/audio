package com.ainoe.audio.reader;

import com.ainoe.audio.constant.AudioFormat;
import com.ainoe.audio.dto.AudioVo;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FlacReader implements IAudioReader {

    @Override
    public String getName() {
        return AudioFormat.FLAC.getValue();
    }

    @Override
    public AudioVo getAudioMetadata(File file) throws Exception {
        FlacFileReader flacFileReader = new FlacFileReader();
        AudioFile read = flacFileReader.read(file);
        Tag tag = read.getTag();
        AudioHeader audioHeader = read.getAudioHeader();
        String format = audioHeader.getFormat();
        int trackLength = audioHeader.getTrackLength();
        String channels = audioHeader.getChannels();
        String cover = null;
        // 封面数据过大，暂不读取
//        Artwork artwork = tag.getFirstArtwork();
//        if (artwork != null) {
//            cover = "data:image/png;base64," + Base64.encodeBase64String(artwork.getBinaryData());
//        }
        return new AudioVo(file.getName()
                , format != null ? format.toLowerCase() : null
                , audioHeader.getSampleRateAsNumber()
                , audioHeader.getBitRateAsNumber()
                , channels != null ? Integer.valueOf(channels) : null
                , (trackLength / 60) + ":" + (trackLength % 60)
                , tag.getFirst(FieldKey.YEAR)
                , tag.getFirst(FieldKey.ARTIST)
                , tag.getFirst(FieldKey.ALBUM)
                , tag.getFirst(FieldKey.TITLE)
                , file.length()
                , cover);
    }
}
