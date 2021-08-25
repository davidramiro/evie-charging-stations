package de.volkswagen.springevchargingflagsapi;

import de.volkswagen.springevchargingflagsapi.model.Flag;
import de.volkswagen.springevchargingflagsapi.model.FlagDto;
import de.volkswagen.springevchargingflagsapi.model.FlagList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/flag")
public class FlagController {
    FlagService flagService;
    private final FlagToDto flagToDto = new FlagToDto();

    public FlagController(FlagService flagService) {
        this.flagService = flagService;
    }

    @GetMapping("/flags")
    @ResponseStatus(HttpStatus.OK)
    public FlagList getFlags() {
        return this.flagService.getFlags();
    }

    @GetMapping("/flag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Flag getFlag(@PathVariable int id) {
        return this.flagService.getFlag(id);
    }

    @PostMapping("/flags")
    @ResponseStatus(HttpStatus.OK)
    public Flag postFlag(@RequestBody FlagDto flagDto) {
        return this.flagService.postFlag(Objects.requireNonNull(flagToDto.convert(flagDto)));
    }

    @PutMapping("/flag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Flag putFlag(@PathVariable int id,
                        @RequestBody FlagDto flagToDto) {
        return this.flagService.putFlag(id, Objects.requireNonNull(this.flagToDto.convert(flagToDto)));
    }

    @DeleteMapping("/flag/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFlag(@PathVariable int id) {
        this.flagService.deleteFlag(id);
    }
}
