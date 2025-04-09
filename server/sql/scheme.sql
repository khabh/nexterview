CREATE TABLE interview (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           title VARCHAR(255) NOT NULL
);

CREATE TABLE prompt (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        instruction VARCHAR(255) NOT NULL,
                        topic VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE prompt_query (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              prompt_id BIGINT NOT NULL,
                              query VARCHAR(255) NOT NULL,
                              CONSTRAINT fk_prompt_query_prompt
                                  FOREIGN KEY (prompt_id) REFERENCES prompt(id)
);

CREATE TABLE dialogue (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          interview_id BIGINT NOT NULL,
                          question VARCHAR(255) NOT NULL,
                          answer VARCHAR(255) NOT NULL,
                          CONSTRAINT fk_dialogue_interview
                              FOREIGN KEY (interview_id) REFERENCES interview(id)
);

CREATE TABLE prompt_answer (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               interview_id BIGINT NOT NULL,
                               query_id BIGINT NOT NULL,
                               answer VARCHAR(255),
                               CONSTRAINT fk_prompt_answer_interview
                                   FOREIGN KEY (interview_id) REFERENCES interview(id),
                               CONSTRAINT fk_prompt_answer_query
                                   FOREIGN KEY (query_id) REFERENCES prompt_query(id)
);
