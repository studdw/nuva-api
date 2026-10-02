-- =========================================================
-- NUVA - Guardioes da Luz | Script Oracle
-- =========================================================
DROP TABLE tb_nuva_conversao CASCADE CONSTRAINTS;
DROP TABLE tb_nuva_validacao CASCADE CONSTRAINTS;
DROP TABLE tb_nuva_contato   CASCADE CONSTRAINTS;
DROP TABLE tb_nuva_missao    CASCADE CONSTRAINTS;
DROP TABLE tb_nuva_usuario   CASCADE CONSTRAINTS;
DROP TABLE tb_nuva_liga      CASCADE CONSTRAINTS;
DROP SEQUENCE sq_nuva_liga;
DROP SEQUENCE sq_nuva_usuario;
DROP SEQUENCE sq_nuva_missao;
DROP SEQUENCE sq_nuva_validacao;
DROP SEQUENCE sq_nuva_conversao;
DROP SEQUENCE sq_nuva_contato;

CREATE SEQUENCE sq_nuva_liga START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_nuva_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_nuva_missao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_nuva_validacao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_nuva_conversao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_nuva_contato START WITH 1 INCREMENT BY 1;

CREATE TABLE tb_nuva_liga (
    cd_liga              NUMBER(10)    CONSTRAINT pk_nuva_liga PRIMARY KEY,
    nm_liga              VARCHAR2(80)  NOT NULL,
    ds_regiao            VARCHAR2(80)  NOT NULL,
    nr_max_participantes NUMBER(4)     DEFAULT 200 NOT NULL
);

CREATE TABLE tb_nuva_usuario (
    cd_usuario       NUMBER(10)    CONSTRAINT pk_nuva_usuario PRIMARY KEY,
    nm_usuario       VARCHAR2(100) NOT NULL,
    ds_email         VARCHAR2(120) NOT NULL CONSTRAINT uk_nuva_usuario_email UNIQUE,
    cd_liga          NUMBER(10)    CONSTRAINT fk_nuva_usuario_liga REFERENCES tb_nuva_liga(cd_liga),
    nr_saldo         NUMBER(10)    DEFAULT 0 NOT NULL,
    nr_pontos_semana NUMBER(10)    DEFAULT 0 NOT NULL,
    dt_cadastro      DATE          DEFAULT SYSDATE NOT NULL
);

CREATE TABLE tb_nuva_missao (
    cd_missao      NUMBER(10)     CONSTRAINT pk_nuva_missao PRIMARY KEY,
    nm_titulo      VARCHAR2(120)  NOT NULL,
    ds_categoria   VARCHAR2(40)   NOT NULL,
    ds_cor         VARCHAR2(20)   DEFAULT 'iris' NOT NULL,
    nr_recompensa  NUMBER(5)      NOT NULL,
    ds_duracao     VARCHAR2(20)   NOT NULL,
    ds_dificuldade VARCHAR2(20)   NOT NULL,
    ds_resumo      VARCHAR2(300)  NOT NULL,
    ds_passos      VARCHAR2(1000) NOT NULL,
    ds_criterio    VARCHAR2(300)  NOT NULL,
    ds_impacto     VARCHAR2(200),
    st_ativa       CHAR(1)        DEFAULT 'S' NOT NULL CONSTRAINT ck_nuva_missao_ativa CHECK (st_ativa IN ('S','N'))
);

CREATE TABLE tb_nuva_validacao (
    cd_validacao NUMBER(10)    CONSTRAINT pk_nuva_validacao PRIMARY KEY,
    cd_usuario   NUMBER(10)    NOT NULL CONSTRAINT fk_nuva_valid_usuario REFERENCES tb_nuva_usuario(cd_usuario) ON DELETE CASCADE,
    cd_missao    NUMBER(10)    NOT NULL CONSTRAINT fk_nuva_valid_missao REFERENCES tb_nuva_missao(cd_missao) ON DELETE CASCADE,
    nm_arquivo   VARCHAR2(200) NOT NULL,
    st_validacao VARCHAR2(10)  NOT NULL CONSTRAINT ck_nuva_valid_status CHECK (st_validacao IN ('VALIDADO','RECUSADO')),
    dt_envio     DATE          DEFAULT SYSDATE NOT NULL
);

CREATE TABLE tb_nuva_conversao (
    cd_conversao NUMBER(10)   CONSTRAINT pk_nuva_conversao PRIMARY KEY,
    cd_usuario   NUMBER(10)   NOT NULL CONSTRAINT fk_nuva_conv_usuario REFERENCES tb_nuva_usuario(cd_usuario) ON DELETE CASCADE,
    nr_coins     NUMBER(10)   NOT NULL CONSTRAINT ck_nuva_conv_min CHECK (nr_coins >= 100),
    vl_desconto  NUMBER(10,2) NOT NULL,
    dt_conversao DATE         DEFAULT SYSDATE NOT NULL
);

CREATE TABLE tb_nuva_contato (
    cd_contato  NUMBER(10)     CONSTRAINT pk_nuva_contato PRIMARY KEY,
    nm_contato  VARCHAR2(100)  NOT NULL,
    ds_email    VARCHAR2(120)  NOT NULL,
    ds_assunto  VARCHAR2(60)   NOT NULL,
    ds_mensagem VARCHAR2(1000) NOT NULL,
    dt_envio    DATE           DEFAULT SYSDATE NOT NULL
);

-- ---------------- DADOS INICIAIS ----------------
INSERT INTO tb_nuva_liga VALUES (sq_nuva_liga.NEXTVAL, 'Liga Vila Mariana', 'São Paulo · Zona Sul', 200);
INSERT INTO tb_nuva_liga VALUES (sq_nuva_liga.NEXTVAL, 'Liga Pinheiros', 'São Paulo · Zona Oeste', 200);
INSERT INTO tb_nuva_liga VALUES (sq_nuva_liga.NEXTVAL, 'Liga Mooca', 'São Paulo · Zona Leste', 200);

INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Guardião Demo', 'demo@nuva.app', 1, 1820, 1820);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Ana P.', 'ana@nuva.app', 1, 2140, 2140);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Rafael M.', 'rafael@nuva.app', 1, 1985, 1985);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Juliana S.', 'juliana@nuva.app', 1, 1700, 1700);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Marina T.', 'marina@nuva.app', 2, 2310, 2310);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Pedro H.', 'pedro@nuva.app', 2, 2105, 2105);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Thiago N.', 'thiago@nuva.app', 3, 2050, 2050);
INSERT INTO tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) VALUES (sq_nuva_usuario.NEXTVAL, 'Camila F.', 'camila@nuva.app', 3, 1990, 1990);

INSERT INTO tb_nuva_missao VALUES (sq_nuva_missao.NEXTVAL, 'Separe os recicláveis da semana', 'Reciclagem', 'iris', 120, '10 min', 'Fácil',
 'Separe plástico, papel, vidro e metal em recipientes distintos e mostre o resultado.',
 'Reúna os resíduos recicláveis da casa.|Separe por tipo de material em recipientes diferentes.|Grave um vídeo de até 30s com seu rosto visível mostrando a separação.',
 'A IA identifica ao menos três tipos de material separados e o rosto do usuário.', 'Até 4 kg de resíduos desviados do aterro por semana.', 'S');
INSERT INTO tb_nuva_missao VALUES (sq_nuva_missao.NEXTVAL, 'Banho de cinco minutos', 'Água', 'periwinkle', 80, '5 min', 'Fácil',
 'Use um timer e mostre o chuveiro desligado ao final dos cinco minutos.',
 'Ative um cronômetro de 5 minutos antes do banho.|Desligue o chuveiro ao fim do tempo.|Grave o cronômetro zerado e o registro fechado.',
 'Cronômetro visível marcando no máximo 5:00 e chuveiro desligado.', 'Economia de até 45 litros de água por banho.', 'S');
INSERT INTO tb_nuva_missao VALUES (sq_nuva_missao.NEXTVAL, 'Caça ao modo stand-by', 'Energia', 'pale-iris', 100, '15 min', 'Média',
 'Desligue da tomada aparelhos que ficam em stand-by durante a noite.',
 'Identifique TV, carregadores e eletrônicos em stand-by.|Retire-os da tomada ou desligue a régua.|Grave os aparelhos desconectados.',
 'Ao menos três aparelhos desconectados aparecem no vídeo.', 'Stand-by pode representar até 12% da conta de energia.', 'S');
INSERT INTO tb_nuva_missao VALUES (sq_nuva_missao.NEXTVAL, 'Vá de bike, a pé ou transporte público', 'Mobilidade', 'orchid', 150, '30 min', 'Desafiadora',
 'Troque um trajeto de carro por uma alternativa de baixa emissão.',
 'Escolha um trajeto que você faria de carro.|Faça-o de bicicleta, a pé ou de transporte público.|Grave um trecho do percurso com seu rosto visível.',
 'Vídeo em movimento com bicicleta, calçada ou veículo público identificáveis.', 'Cerca de 2 kg de CO2 evitados a cada 10 km.', 'S');
INSERT INTO tb_nuva_missao VALUES (sq_nuva_missao.NEXTVAL, 'Um dia de luz natural', 'Energia', 'deep-iris', 90, '1 dia', 'Média',
 'Mantenha as lâmpadas apagadas enquanto houver luz do dia no cômodo.',
 'Abra cortinas e janelas pela manhã.|Mantenha as lâmpadas desligadas até o anoitecer.|Grave o ambiente iluminado naturalmente com os interruptores desligados.',
 'Ambiente iluminado por luz natural e interruptores desligados visíveis.', 'Redução de até 1 kWh por cômodo ao dia.', 'S');

INSERT INTO tb_nuva_conversao VALUES (sq_nuva_conversao.NEXTVAL, 1, 500, 5.00, SYSDATE - 15);
COMMIT;
