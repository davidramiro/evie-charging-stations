package de.volkswagen.springevchargingflagsapi;

import de.volkswagen.springevchargingflagsapi.model.Flag;
import de.volkswagen.springevchargingflagsapi.model.FlagList;
import de.volkswagen.springevchargingflagsapi.model.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlagServiceTests {
    private FlagService flagService;
    @Mock
    FlagRepository flagRepository;

    private List<Flag> flagList;
    private final int FLAG_COUNT = 100;


    @BeforeEach
    void setUp() {
        this.flagService = new FlagService(this.flagRepository);
        this.flagList = new ArrayList<>();
        for(float i=1; i <= FLAG_COUNT; i++) {
            Flag flag = new Flag((int) i, StatusEnum.IN_USE, 100+i, 100+i, 100+i, 100+i);
            this.flagList.add(flag);
        }
    }

    @Test
    void getFlags_noParams_exists_returnFlagList() {
        when(this.flagRepository.findAll()).thenReturn(this.flagList);
        FlagList dbFlagList = this.flagService.getFlags();
        assertThat(dbFlagList).isNotNull();
        assertThat(dbFlagList.getFlags().isEmpty()).isFalse();
        assertThat(dbFlagList.getFlags().containsAll(this.flagList)).isTrue();

    }

    @Test
    void getFlags_empty_returnsNotFound() {
        when(this.flagRepository.findAll()).thenReturn(new ArrayList<>());
        assertThatExceptionOfType(FlagNotFoundException.class).isThrownBy(() -> this.flagService.getFlags());
    }

    @Test
    void getFlag_withParamId_exists_returnsFlag() {
        final int TEST_ID = 1;
        when(this.flagRepository.findById(TEST_ID)).thenReturn(Optional.of(this.flagList.get(TEST_ID)));
        Flag flag = this.flagService.getFlag(TEST_ID);
        assertThat(flag).isNotNull();
        assertThat(flag.getStatus().equals(StatusEnum.IN_USE)).isTrue();
    }

    @Test
    void getFlag_withParamId_notExists_returnsNotFound() {
        when(this.flagRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThatExceptionOfType(FlagNotFoundException.class).isThrownBy(() -> this.flagService.getFlag(anyInt()));
    }


    @Test
    void postFlag_valid_returnsFlag() {
        Flag flag = new Flag(2, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        when(this.flagRepository.save(flag)).thenReturn(flag);
        assertThat(this.flagService.postFlag(flag)).isEqualTo(flag);
    }


    @Test
    void putFlag_exists_valid_returnFlag() {
        final Flag flag = new Flag(1,4, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        final Flag newFlag = new Flag(1,3, StatusEnum.MAINTENANCE, 50f, 50f, 50f, 50f);
        when(this.flagRepository.findById(flag.getId())).thenReturn(Optional.of(flag));
        when(this.flagRepository.save(flag)).thenReturn(flag);
        final float newCcs2 = this.flagService.putFlag(flag.getId(), newFlag).getCcs2();
        assertThat(newFlag.getCcs2()).isEqualTo(newCcs2);
    }

    @Test
    void patchFlag_notExists_valid_returnNotFound() {
        when(this.flagRepository.findById(anyInt())).thenReturn(Optional.empty());
        // TODO: Check raw/any value exception in lambda (why does this work?)
        assertThatExceptionOfType(FlagNotFoundException.class).isThrownBy(() -> this.flagService.putFlag(anyInt(),new Flag()));
    }

    @Test
    void deleteFlag_exists_returnOk() {
        Flag flag = new Flag(2, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        when(this.flagRepository.findById(anyInt())).thenReturn(Optional.of(flag));
        this.flagService.deleteFlag(anyInt());
        verify(flagRepository).delete(any(Flag.class));
    }

    @Test
    void deleteFlag_notExists_returnNotFound() {
        assertThatExceptionOfType(FlagNotFoundException.class).isThrownBy(() -> this.flagService.deleteFlag(anyInt()));
    }
}
