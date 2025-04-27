INSERT INTO USERS (email, password, nickname)
VALUES ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin');

INSERT INTO PROMPT(id, topic, instruction)
VALUES (1, '자유 형식', '사용자가 원하는 인터뷰 질문 내용을 참고해 질문을 생성해줘: '),
       (2, '인성 관련', '다음에 제공되는 지원자 관련 정보를 참고해서 면접에서 예상되는 인성 질문 생성해줘.'),
       (3, '지원 공고 기반', '다음에 제공되는 공고 내용과 지원자의 경험을 바탕으로 질문 생성해줘.'),
       (4, '프로젝트 관련', '다음에 제공되는 프로젝트 관련 설명을 기반으로 예상되는 CS 질문 생성해줘.');

INSERT INTO PROMPT_QUERY(prompt_id, `query`)
VALUES (1, '원하는 인터뷰 질문 내용'),
       (2, '자신의 장점과 단점'),
       (2, '협업 및 문제 해결 경험'),
       (3, '지원 직무의 공고 내용(주요 업무/자격 요건/우대 사항)'),
       (3, '지원 공고와 관련된 경험(프로젝트/대회/교육 프로그램)'),
       (4, '진행한 프로젝트에 대한 설명'),
       (4, '사용한 기술 스택'),
       (4, '자신이 프로젝트에서 맡은 업무');

INSERT INTO INTERVIEW(title, user_id)
VALUES ('인터뷰 제목', 1);

INSERT INTO PROMPT_ANSWER(query_id, interview_id, answer)
values (2, 1, '저의 장점과 단점은 xxx입니다.'),
       (3, 1, '저의 문제 해결 경험은 xxx입니다.');

INSERT INTO DIALOGUE(question, answer, interview_id)
VALUES ('질문1', '답변1', 1),
       ('질문2', '답변2', 1),
       ('질문3', '답변3', 1);

