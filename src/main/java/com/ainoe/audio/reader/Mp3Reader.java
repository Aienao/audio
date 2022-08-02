package com.ainoe.audio.reader;

import com.ainoe.audio.constant.AudioFormat;
import com.ainoe.audio.dto.AudioVo;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Mp3Reader implements IAudioReader {

    @Override
    public String getName() {
        return AudioFormat.MP3.getValue();
    }

    @Override
    public AudioVo getAudioMetadata(File file) throws Exception {
        MP3File mp3File = new MP3File(file);
        AudioHeader audioHeader = mp3File.getAudioHeader();
        int trackLength = audioHeader.getTrackLength();
        String format = audioHeader.getFormat();
        AbstractID3v2Tag tag = mp3File.getID3v2Tag();
        String cover = null;
        // 封面数据过大，暂不读取
//        Artwork artwork = tag.getFirstArtwork();
//        if (artwork != null) {
//            cover = "data:image/png;base64," + Base64.encodeBase64String(artwork.getBinaryData());
//        }
        // 暂无法获取声道数
        return new AudioVo(file.getName()
                , format != null ? format.toLowerCase() : null
                , audioHeader.getSampleRateAsNumber()
                , audioHeader.getBitRateAsNumber()
                , null
                , (trackLength / 60) + ":" + (trackLength % 60)
                , tag.getFirst(ID3v24Frames.FRAME_ID_YEAR)
                , tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST)
                , tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM)
                , tag.getFirst(ID3v24Frames.FRAME_ID_TITLE)
                , file.length()
                , cover);
    }
}
