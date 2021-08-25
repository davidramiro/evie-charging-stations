package de.volkswagen.springevchargingflagsapi;

import de.volkswagen.springevchargingflagsapi.model.Flag;
import de.volkswagen.springevchargingflagsapi.model.FlagList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FlagService {
    private final FlagRepository flagRepository;

    public FlagService(FlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public FlagList getFlags() {
        List<Flag> flagList = this.flagRepository.findAll();
        if (flagList.isEmpty()) {
            throw new FlagNotFoundException();
        }
        return new FlagList(flagList);
    }

    public Flag getFlag(int flagId) {
        return this.flagRepository.findById(flagId).orElseThrow(FlagNotFoundException::new);
    }

    public Flag postFlag(Flag flag) {
        if(Objects.isNull(flag.getCcs2())) {
            throw new InvalidParameterException();
        }
        return this.flagRepository.save(flag);
    }

    public Flag putFlag(int id, Flag flag) {
        Flag dbFlag = this.flagRepository.findById(id).orElseThrow(FlagNotFoundException::new);
        dbFlag.setLocationId(flag.getLocationId());
        dbFlag.setStatus(flag.getStatus());
        dbFlag.setCcs2(flag.getCcs2());
        dbFlag.setChademo(flag.getChademo());
        dbFlag.setTesla(flag.getTesla());
        dbFlag.setType2(flag.getType2());
        return this.flagRepository.save(dbFlag);
    }

    public void deleteFlag(int id) {
        Flag flag = this.flagRepository.findById(id).orElseThrow(FlagNotFoundException::new);
        this.flagRepository.delete(flag);
    }
}
