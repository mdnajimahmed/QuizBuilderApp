-- insert your migration script here
create table options (
       id uuid not null,
        created_at timestamp(6),
        created_by varchar(255),
        updated_at timestamp(6),
        updated_by varchar(255),
        version integer,
        is_correct boolean not null,
        text varchar(255),
        question_id uuid not null,
        primary key (id)
    );

create table question_attempts (
       id uuid not null,
        created_at timestamp(6),
        created_by varchar(255),
        updated_at timestamp(6),
        updated_by varchar(255),
        version integer,
        score float(53) not null,
        selected_option_ids varchar(255),
        skipped boolean not null,
        question_id uuid not null,
        quiz_attempt_id uuid not null,
        primary key (id)
    );

    create table questions (
       id uuid not null,
        created_at timestamp(6),
        created_by varchar(255),
        updated_at timestamp(6),
        updated_by varchar(255),
        version integer,
        text varchar(255),
        quiz_id uuid not null,
        primary key (id)
    );

create table quiz_attempts (
       id uuid not null,
        created_at timestamp(6),
        created_by varchar(255),
        updated_at timestamp(6),
        updated_by varchar(255),
        version integer,
        score float(53) not null,
        quiz_id uuid not null,
        primary key (id)
    );
create table quizzes (
       id uuid not null,
        created_at timestamp(6),
        created_by varchar(255),
        updated_at timestamp(6),
        updated_by varchar(255),
        version integer,
        published boolean not null,
        published_at timestamp(6),
        title varchar(255),
        primary key (id)
    );



ALTER TABLE IF EXISTS options
    ADD CONSTRAINT fk_options_question_id
    FOREIGN KEY (question_id) REFERENCES questions(id);

ALTER TABLE IF EXISTS question_attempts
    ADD CONSTRAINT fk_question_attempts_question_id
    FOREIGN KEY (question_id) REFERENCES questions(id);


ALTER TABLE IF EXISTS question_attempts
    ADD CONSTRAINT fk_question_attempts_quiz_attempt_id
    FOREIGN KEY (quiz_attempt_id) REFERENCES quiz_attempts(id);


ALTER TABLE IF EXISTS questions
    ADD CONSTRAINT fk_questions_quiz_id
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id);


ALTER TABLE IF EXISTS quiz_attempts
    ADD CONSTRAINT fk_quiz_attempts_quiz_id
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id);


CREATE INDEX quiz_attempt_quiz_createdby_idx ON quiz_attempts (quiz_id, created_by);
CREATE INDEX quiz_attempt_createdby_idx ON quiz_attempts (created_by);
CREATE INDEX quiz_attempt_quiz_idx ON quiz_attempts (quiz_id);