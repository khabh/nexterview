package com.nexterview.server.controller.exception;

import com.nexterview.server.exception.NexterviewErrorCode;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class ErrorCodeHttpStatus {

    private static final Map<NexterviewErrorCode, HttpStatus> ERROR_CODE_TO_STATUS = new EnumMap<>(
            NexterviewErrorCode.class);

    static {
        // 400 Bad Request
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.DIALOGUE_QUESTION_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.DIALOGUE_ANSWER_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_TOPIC_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_INSTRUCTION_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_QUERY_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_QUERY_NULL, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_ANSWER_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_NULL, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.INTERVIEW_NULL, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.INTERVIEW_TITLE_INVALID, HttpStatus.BAD_REQUEST);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_ANSWER_REQUIRED, HttpStatus.BAD_REQUEST);

        // 404 Not Found
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.PROMPT_NOT_FOUND, HttpStatus.NOT_FOUND);
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.INTERVIEW_NOT_FOUND, HttpStatus.NOT_FOUND);

        // 409 Conflict
        ERROR_CODE_TO_STATUS.put(NexterviewErrorCode.EMAIL_DUPLICATED, HttpStatus.CONFLICT);
    }
    
    public static HttpStatus getHttpStatus(NexterviewErrorCode errorCode) {
        return ERROR_CODE_TO_STATUS.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
