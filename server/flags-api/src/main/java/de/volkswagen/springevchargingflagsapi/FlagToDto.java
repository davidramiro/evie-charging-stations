package de.volkswagen.springevchargingflagsapi;

import de.volkswagen.springevchargingflagsapi.model.Flag;
import de.volkswagen.springevchargingflagsapi.model.FlagDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FlagToDto implements Converter<FlagDto, Flag> {
    @Override
    public Flag convert(FlagDto source) {
        Flag flag = new Flag();
        flag.setCcs2(source.getCcs2());
        flag.setChademo(source.getChademo());
        flag.setTesla(source.getTesla());
        flag.setType2(source.getType2());
        flag.setStatus(source.getStatus());
        flag.setLocationId(source.getLocationId());
        return flag;
    }
}
