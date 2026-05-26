package com.acropolis.bfhl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class BfhlController {

    private static final String FULL_NAME   = "Ritik Soni";
    private static final String DOB         = "15032005";          
    private static final String EMAIL       = "ritiksoni230452@acropolis.in";
    private static final String ROLL_NUMBER = "0827RL231053";

    private static final String USER_ID;
    static {
        String[] parts = FULL_NAME.trim().toLowerCase().split("\\s+");
        USER_ID = (parts.length >= 2)
                ? parts[0] + "_" + parts[1] + "_" + DOB
                : parts[0] + "_" + DOB;
    }

    public static class DataRequest {
        @JsonProperty("data")
        private List<String> data;

        public List<String> getData() { return data; }
        public void setData(List<String> data) { this.data = data; }
    }

    public static class DataResponse {

        @JsonProperty("is_success")
        private boolean isSuccess;

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("email")
        private String email;

        @JsonProperty("roll_number")
        private String rollNumber;

        @JsonProperty("odd_numbers")
        private List<String> oddNumbers;

        @JsonProperty("even_numbers")
        private List<String> evenNumbers;

        @JsonProperty("alphabets")
        private List<String> alphabets;

        @JsonProperty("special_characters")
        private List<String> specialCharacters;

        @JsonProperty("sum")
        private String sum;

        @JsonProperty("concat_string")
        private String concatString;

    

        public boolean isSuccess() { return isSuccess; }
        public void setSuccess(boolean success) { isSuccess = success; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRollNumber() { return rollNumber; }
        public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

        public List<String> getOddNumbers() { return oddNumbers; }
        public void setOddNumbers(List<String> oddNumbers) { this.oddNumbers = oddNumbers; }

        public List<String> getEvenNumbers() { return evenNumbers; }
        public void setEvenNumbers(List<String> evenNumbers) { this.evenNumbers = evenNumbers; }

        public List<String> getAlphabets() { return alphabets; }
        public void setAlphabets(List<String> alphabets) { this.alphabets = alphabets; }

        public List<String> getSpecialCharacters() { return specialCharacters; }
        public void setSpecialCharacters(List<String> specialCharacters) { this.specialCharacters = specialCharacters; }

        public String getSum() { return sum; }
        public void setSum(String sum) { this.sum = sum; }

        public String getConcatString() { return concatString; }
        public void setConcatString(String concatString) { this.concatString = concatString; }
    }

    @PostMapping("/bfhl")
    public ResponseEntity<DataResponse> process(@RequestBody DataRequest request) {

        DataResponse response = new DataResponse();
        response.setUserId(USER_ID);
        response.setEmail(EMAIL);
        response.setRollNumber(ROLL_NUMBER);

        List<String> data = (request != null) ? request.getData() : null;

       
        if (data == null || data.isEmpty()) {
            response.setSuccess(false);
            response.setOddNumbers(Collections.emptyList());
            response.setEvenNumbers(Collections.emptyList());
            response.setAlphabets(Collections.emptyList());
            response.setSpecialCharacters(Collections.emptyList());
            response.setSum("0");
            response.setConcatString("");
            return ResponseEntity.ok(response);
        }

        List<String> oddNumbers       = new ArrayList<>();
        List<String> evenNumbers      = new ArrayList<>();
        List<String> alphabets        = new ArrayList<>();
        List<String> specialChars     = new ArrayList<>();

        long totalSum = 0;

        // Collect all alphabetical characters (flattened) in input order
        List<Character> allLetters = new ArrayList<>();

        for (String element : data) {
            if (element == null) continue;

            if (isNumeric(element)) {
                // Pure numeric element
                long num = Long.parseLong(element);
                totalSum += num;
                if (num % 2 == 0) {
                    evenNumbers.add(element);
                } else {
                    oddNumbers.add(element);
                }
            } else if (isAlphabetic(element)) {
                // Pure alphabetic element (single or multi-char)
                alphabets.add(element.toUpperCase());
                // Flatten individual letters for concat logic
                for (char c : element.toCharArray()) {
                    allLetters.add(c);
                }
            } else {
                // Special character (anything else)
                specialChars.add(element);
            }
        }

       
        String sumStr = String.valueOf(totalSum);

        // ---- Concat string ----
        // 1. Reverse the flattened letter sequence
        Collections.reverse(allLetters);
        // 2. Apply alternating case starting at index 0 → UPPER
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allLetters.size(); i++) {
            char c = allLetters.get(i);
            sb.append((i % 2 == 0) ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        String concatString = sb.toString();

        response.setSuccess(true);
        response.setOddNumbers(oddNumbers);
        response.setEvenNumbers(evenNumbers);
        response.setAlphabets(alphabets);
        response.setSpecialCharacters(specialChars);
        response.setSum(sumStr);
        response.setConcatString(concatString);

        return ResponseEntity.ok(response);
    }

 
    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /** Returns true if the string consists entirely of letter characters. */
    private boolean isAlphabetic(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("is_success", false, "message", "Invalid request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("is_success", false, "message", "Internal server error"));
    }
}
