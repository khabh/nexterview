INSERT INTO USERS (email, password, nickname)
VALUES ('aaa@aaa.com', '$2b$12$iBwdrWzHyu.Ds53Zp8JBq.TneXYKJsDbGgATWrxIlpK70Eu6IQQxO', 'admin');

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

-- 첫 번째 인터뷰
INSERT INTO INTERVIEW(title, user_id)
VALUES ('백엔드 개발자 면접 준비', 1);

INSERT INTO PROMPT_ANSWER(query_id, interview_id, answer)
VALUES (2, 1, '저의 강점은 문제 해결 능력과 책임감이며, 단점은 가끔 완벽주의적인 성향이 있다는 점입니다.'),
       (3, 1, '사용자 로그인 오류 이슈를 디버깅하여 원인을 파악하고, 세션 관리 로직을 개선한 경험이 있습니다.');

INSERT INTO DIALOGUE(question, answer, interview_id)
VALUES ('자기소개 부탁드립니다.', '저는 3년차 백엔드 개발자이며, Java와 Spring을 중심으로 다양한 서비스를 개발해왔습니다.', 1),
       ('최근에 해결한 기술적인 문제는 무엇인가요?', '로그인 시 세션이 유지되지 않는 문제를 해결한 경험이 있습니다. 원인은 Redis 설정 누락이었습니다.', 1),
       ('협업 시 중요하게 생각하는 가치는 무엇인가요?', '원활한 커뮤니케이션과 책임감이라고 생각합니다.', 1);


-- 두 번째 인터뷰
INSERT INTO INTERVIEW(title, user_id)
VALUES ('스타트업 프론트엔드 면접', 1);

INSERT INTO PROMPT_ANSWER(query_id, interview_id, answer)
VALUES (2, 2, '장점은 사용자 경험을 고려한 UI 설계이며, 단점은 아직 테스트 코드 작성에 익숙하지 않다는 점입니다.'),
       (3, 2, '실시간 채팅 기능 구현 중 WebSocket 연결 문제를 해결하며, 서버-클라이언트 간 메시지 처리 방식을 개선한 경험이 있습니다.');

INSERT INTO DIALOGUE(question, answer, interview_id)
VALUES ('왜 프론트엔드 직무를 선택하셨나요?', '사용자와 가장 가까운 영역에서 가치를 전달할 수 있다는 점에 매력을 느꼈기 때문입니다.', 2),
       ('React에서 상태 관리를 어떻게 하시나요?', '작은 프로젝트에서는 useState와 Context API를, 복잡한 경우에는 Redux를 사용합니다.', 2),
       ('협업에서 발생한 갈등을 어떻게 해결했나요?', '정기적인 회고를 통해 의견을 공유하고, 중재자의 역할을 자처해 해결했습니다.', 2);
