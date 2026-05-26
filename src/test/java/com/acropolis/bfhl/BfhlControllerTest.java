package com.acropolis.bfhl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BfhlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ----------------------------------------------------------------
    // Test 1 — Example A
    // data: ["a","1","334","4","R","$"]
    // odd:["1"], even:["334","4"], alpha:["A","R"], special:["$"]
    // sum:"339", concat:"Ra"
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Example A: mixed letters, numbers, special char")
    void testExampleA() throws Exception {
        String body = """
                {"data": ["a","1","334","4","R","$"]}
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.odd_numbers[0]").value("1"))
                .andExpect(jsonPath("$.odd_numbers.length()").value(1))
                .andExpect(jsonPath("$.even_numbers[0]").value("334"))
                .andExpect(jsonPath("$.even_numbers[1]").value("4"))
                .andExpect(jsonPath("$.even_numbers.length()").value(2))
                .andExpect(jsonPath("$.alphabets[0]").value("A"))
                .andExpect(jsonPath("$.alphabets[1]").value("R"))
                .andExpect(jsonPath("$.alphabets.length()").value(2))
                .andExpect(jsonPath("$.special_characters[0]").value("$"))
                .andExpect(jsonPath("$.special_characters.length()").value(1))
                .andExpect(jsonPath("$.sum").value("339"))
                .andExpect(jsonPath("$.concat_string").value("Ra"));
    }

    // ----------------------------------------------------------------
    // Test 2 — Example B
    // data: ["2","a","y","4","&","-","*","5","92","b"]
    // odd:["5"], even:["2","4","92"], alpha:["A","Y","B"]
    // special:["&","-","*"], sum:"103", concat:"ByA"
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Example B: multi-digit numbers, multiple specials")
    void testExampleB() throws Exception {
        String body = """
                {"data": ["2","a","y","4","&","-","*","5","92","b"]}
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.odd_numbers[0]").value("5"))
                .andExpect(jsonPath("$.odd_numbers.length()").value(1))
                .andExpect(jsonPath("$.even_numbers[0]").value("2"))
                .andExpect(jsonPath("$.even_numbers[1]").value("4"))
                .andExpect(jsonPath("$.even_numbers[2]").value("92"))
                .andExpect(jsonPath("$.even_numbers.length()").value(3))
                .andExpect(jsonPath("$.alphabets[0]").value("A"))
                .andExpect(jsonPath("$.alphabets[1]").value("Y"))
                .andExpect(jsonPath("$.alphabets[2]").value("B"))
                .andExpect(jsonPath("$.alphabets.length()").value(3))
                .andExpect(jsonPath("$.special_characters[0]").value("&"))
                .andExpect(jsonPath("$.special_characters[1]").value("-"))
                .andExpect(jsonPath("$.special_characters[2]").value("*"))
                .andExpect(jsonPath("$.special_characters.length()").value(3))
                .andExpect(jsonPath("$.sum").value("103"))
                .andExpect(jsonPath("$.concat_string").value("ByA"));
    }

    // ----------------------------------------------------------------
    // Test 3 — Example C
    // data: ["A","ABCD","DOE"]
    // odd:[], even:[], alpha:["A","ABCD","DOE"], special:[]
    // sum:"0", concat:"EoDdCbAa"
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Example C: multi-char alphabetic elements only")
    void testExampleC() throws Exception {
        String body = """
                {"data": ["A","ABCD","DOE"]}
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.odd_numbers.length()").value(0))
                .andExpect(jsonPath("$.even_numbers.length()").value(0))
                .andExpect(jsonPath("$.alphabets[0]").value("A"))
                .andExpect(jsonPath("$.alphabets[1]").value("ABCD"))
                .andExpect(jsonPath("$.alphabets[2]").value("DOE"))
                .andExpect(jsonPath("$.alphabets.length()").value(3))
                .andExpect(jsonPath("$.special_characters.length()").value(0))
                .andExpect(jsonPath("$.sum").value("0"))
                .andExpect(jsonPath("$.concat_string").value("EoDdCbAa"));
    }

    // ----------------------------------------------------------------
    // Test 4 — Empty input → is_success: false
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Empty data array returns is_success false")
    void testEmptyInput() throws Exception {
        String body = """
                {"data": []}
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(false))
                .andExpect(jsonPath("$.odd_numbers.length()").value(0))
                .andExpect(jsonPath("$.even_numbers.length()").value(0))
                .andExpect(jsonPath("$.alphabets.length()").value(0))
                .andExpect(jsonPath("$.special_characters.length()").value(0))
                .andExpect(jsonPath("$.sum").value("0"));
    }

    // ----------------------------------------------------------------
    // Test 5 — Mixed multi-digit numbers only
    // data: ["100","7","22"]
    // even:["100","22"], odd:["7"], sum:"129"
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Multi-digit numbers: even/odd split and sum")
    void testMultiDigitNumbers() throws Exception {
        String body = """
                {"data": ["100","7","22"]}
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.even_numbers[0]").value("100"))
                .andExpect(jsonPath("$.even_numbers[1]").value("22"))
                .andExpect(jsonPath("$.even_numbers.length()").value(2))
                .andExpect(jsonPath("$.odd_numbers[0]").value("7"))
                .andExpect(jsonPath("$.odd_numbers.length()").value(1))
                .andExpect(jsonPath("$.alphabets.length()").value(0))
                .andExpect(jsonPath("$.special_characters.length()").value(0))
                .andExpect(jsonPath("$.sum").value("129"))
                .andExpect(jsonPath("$.concat_string").value(""));
    }
}
