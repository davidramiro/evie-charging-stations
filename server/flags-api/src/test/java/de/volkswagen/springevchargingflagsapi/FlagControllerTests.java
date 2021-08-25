package de.volkswagen.springevchargingflagsapi;

import de.volkswagen.springevchargingflagsapi.model.Flag;
import de.volkswagen.springevchargingflagsapi.model.FlagList;
import de.volkswagen.springevchargingflagsapi.model.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlagController.class)
class FlagControllerTests {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    FlagService flagService;

    private List<Flag> flagList;
    private final int FLAG_COUNT = 100;


    @BeforeEach
    void setUp() {
        this.flagList = new ArrayList<Flag>();
        for(float i=1; i <= FLAG_COUNT; i++) {
            Flag flag = new Flag((int) i, StatusEnum.IN_USE, 100+i, 100+i, 100+i, 100+i);
            this.flagList.add(flag);
        }
    }

    @Test
    void getFlags_noParams_exists_returnFlagList() throws Exception {
        when(flagService.getFlags()).thenReturn(new FlagList(this.flagList));
        mockMvc.perform(get("/api/flag/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flags", hasSize(FLAG_COUNT)));
    }

    @Test
    void getFlags_empty_returnsNotFound() throws Exception {
        when(flagService.getFlags()).thenThrow(FlagNotFoundException.class);

        mockMvc.perform(get("/api/flag/flags"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFlag_withParamId_exists_returnsFlag() throws Exception {
        Flag flag = new Flag(2, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        when(flagService.getFlag(anyInt())).thenReturn(flag);
        mockMvc.perform(get("/api/flag/flag/" + 1, Flag.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("locationid").value(flag.getLocationId()));
    }

    @Test
    void getFlag_withParamId_notExists_returnsNotFound() throws Exception {
        when(flagService.getFlag(anyInt())).thenThrow(FlagNotFoundException.class);

        mockMvc.perform(get("/api/flag/flag/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFlag_withInvalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/flag/flag/something"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postFlag_valid_returnsFlag() throws Exception {
        Flag flag = new Flag(2, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        when(flagService.postFlag(any(Flag.class))).thenReturn(flag);
        mockMvc.perform(post("/api/flag/flags")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(flag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("locationid").value(2))
                .andExpect(jsonPath("ccs2").value(100));

    }

    @Test
    void postFlag_invalid_returnsBadRequest() throws Exception {
        when(flagService.postFlag(new Flag())).thenThrow(InvalidParameterException.class);
        mockMvc.perform(post("/api/flag/flags").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString("invalidInput")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchFlag_exists_valid_returnFlag() throws Exception {
        Flag flag = new Flag(1, 2, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        Flag newFlag = new Flag(3, StatusEnum.IN_USE, 10f, 10f, 10f, 10f);
        when(flagService.putFlag(anyInt(), any(Flag.class))).thenReturn(newFlag);
        mockMvc.perform(put("/api/flag/flag/" + flag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newFlag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("chademo").value(10));

    }

    @Test
    void putFlag_notExists_valid_returnNotFound() throws Exception{
        Flag flag = new Flag(2, StatusEnum.IN_USE, 100f, 100f, 100f, 100f);
        Flag newFlag = new Flag(3, StatusEnum.IN_USE, 10f, 10f, 10f, 10f);
        when(flagService.putFlag(anyInt(), any(Flag.class))).thenThrow(FlagNotFoundException.class);
        mockMvc.perform(put("/api/flag/flag/" + flag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newFlag)))
                .andExpect(status().isNotFound());
    }

    @Test
    void putFlag_invalid_returnBadRequest() throws Exception{
        mockMvc.perform(put("/api/flag/flag/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString("invalid")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFlag_exists_returnOk() throws Exception {
        mockMvc.perform(delete("/api/flag/flag/1"))
                .andExpect(status().isOk());
        verify(flagService).deleteFlag(anyInt());
    }

    @Test
    void deleteFlag_notExists_returnNotFound() throws Exception {
        doThrow(new FlagNotFoundException()).when(flagService).deleteFlag(anyInt());
        mockMvc.perform(delete("/api/flag/flag/1"))
                .andExpect(status().isNotFound());
        verify(flagService).deleteFlag((anyInt()));
    }

    @Test
    void deleteFlag_invalid_returnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/flag/flag/bad"))
                .andExpect(status().isBadRequest());
    }
}
