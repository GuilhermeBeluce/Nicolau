-- ============================================================
-- Cadastro de Contribuintes - Massa de dados ficticia
-- Usa as proprias Stored Procedures de inclusao.
-- Dados fictícios, sem qualquer correspondência com pessoas ou empresas reais.
-- Documentos respeitam apenas tamanho e formato (sem cálculo de dígito verificador).
-- ============================================================

USE cadastro_contribuintes;

CALL sp_contribuinte_incluir('111111111','0000','11','CPF',0,0.00,'1990-01-10','Ana Beatriz Fictícia',NULL,'43','999990001','ana.ficticia@exemplo.com.br','Londrina','PR',@c,@m);
CALL sp_contribuinte_incluir('222222222','0000','22','CPF',0,0.00,'1985-03-22','Bruno Carlos Fictício',NULL,'11','988880002','bruno.ficticio@exemplo.com.br','São Paulo','SP',@c,@m);
CALL sp_contribuinte_incluir('333333333','0000','33','CPF',0,0.00,'1978-07-05','Carla Daniela Fictícia',NULL,'21','977770003','carla.ficticia@exemplo.com.br','Rio de Janeiro','RJ',@c,@m);
CALL sp_contribuinte_incluir('444444444','0000','44','CPF',0,0.00,'2000-11-30','Diego Eduardo Fictício',NULL,'41','966660004','diego.ficticio@exemplo.com.br','Curitiba','PR',@c,@m);
CALL sp_contribuinte_incluir('555555555','0000','55','CPF',0,0.00,'1995-05-15','Elisa Fernanda Fictícia',NULL,'51','955550005','elisa.ficticia@exemplo.com.br','Porto Alegre','RS',@c,@m);
CALL sp_contribuinte_incluir('666666666','0000','66','CPF',0,0.00,'1982-09-18','Fábio Gabriel Fictício',NULL,'19','944440006','fabio.ficticio@exemplo.com.br','Campinas','SP',@c,@m);

CALL sp_contribuinte_incluir('11111111','0001','91','CNPJ',12,1500000.00,'2010-04-12','Alfa Comércio Fictício Ltda','Alfa Comércio','43','333310001','contato@alfaficticio.exemplo.com.br','Londrina','PR',@c,@m);
CALL sp_contribuinte_incluir('22222222','0001','92','CNPJ',45,8750000.50,'2005-08-01','Beta Indústria Fictícia S.A.','Beta Indústria','11','333320002','contato@betaficticio.exemplo.com.br','São Paulo','SP',@c,@m);
CALL sp_contribuinte_incluir('33333333','0001','93','CNPJ',3,250000.00,'2018-02-20','Gama Serviços Fictícios Ltda','Gama Serviços','41','333330003','contato@gamaficticio.exemplo.com.br','Curitiba','PR',@c,@m);
CALL sp_contribuinte_incluir('44444444','0001','94','CNPJ',150,52000000.00,'1999-12-01','Delta Logística Fictícia S.A.','Delta Log','21','333340004','contato@deltaficticio.exemplo.com.br','Rio de Janeiro','RJ',@c,@m);
CALL sp_contribuinte_incluir('55555555','0001','95','CNPJ',8,980000.00,'2015-06-09','Épsilon Consultoria Fictícia Ltda','Épsilon Consultoria','51','333350005','contato@epsilonficticio.exemplo.com.br','Porto Alegre','RS',@c,@m);
CALL sp_contribuinte_incluir('66666666','0001','96','CNPJ',27,4300000.00,'2012-10-25','Zeta Tecnologia Fictícia Ltda','Zeta Tech','19','333360006','contato@zetaficticio.exemplo.com.br','Campinas','SP',@c,@m);

CALL sp_contribuinte_incluir('777777777','0000','77','CPF',0,0.00,'1970-01-01','Gustavo Henrique Fictício',NULL,'85','933330007','gustavo.ficticio@exemplo.com.br','Fortaleza','CE',@c,@m);
CALL sp_contribuinte_incluir('888888888','0000','88','CPF',0,0.00,'1988-04-14','Helena Isabel Fictícia',NULL,'71','922220008','helena.ficticia@exemplo.com.br','Salvador','BA',@c,@m);
CALL sp_contribuinte_incluir('77777777','0001','97','CNPJ',5,610000.00,'2020-01-15','Eta Comunicação Fictícia Ltda','Eta Comunicação','85','333370007','contato@etaficticio.exemplo.com.br','Fortaleza','CE',@c,@m);
