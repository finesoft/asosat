	if not exists (select * from sysobjects where name='AT_ORD' and xtype='U')
	    create table AT_ORD (
	       id bigint not null,
	        evn bigint,
	        msgSeqNum bigint,
	        buyer nvarchar(255),
	        confirmed bit,
	        confirmedTime datetime2,
	        date datetime2,
	        number nvarchar(255),
	        seller nvarchar(255),
	        totalAmount numeric(19,2),
	        primary key (id)
	    )
	go
	
	if not exists (select * from sysobjects where name='AT_ORD_ITM' and xtype='U')
	    create table AT_ORD_ITM (
	       orderId bigint not null,
	        amount numeric(19,2),
	        product nvarchar(255),
	        qty numeric(19,2),
	        unitPrice numeric(19,2),
	        seq int not null,
	        primary key (orderId, seq)
	    )
	go
	
	if not exists (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[hibernate_sequence]') AND type = 'SO')
		create sequence hibernate_sequence start with 1 increment by 1
	go
	
	if exists (select * from sys.objects where object_id = object_id(n'[dbo].[FKq6dchi4q1ljcngxow7lsdlxmf]') and type in (n'u'))
	    alter table AT_ORD_ITM 
	       add constraint FKq6dchi4q1ljcngxow7lsdlxmf 
	       foreign key (orderId) 
	       references AT_ORD
	go
	
