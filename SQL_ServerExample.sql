CREATE DATABASE DB_Ecommerce;

USE DB_Ecommerce;

CREATE TABLE dbo.TBL_Users(

    User_id INT PRIMARY KEY IDENTITY(1,1),
    User_name NVARCHAR(20),
    User_surname NVARCHAR(20),
    User_phone char(11)

);

CREATE TABLE dbo.TBL_Order(

    Order_id INT PRIMARY KEY IDENTITY(1,1),
    User_id INT FOREIGN KEY REFERENCES TBL_Users(User_id),
    Order_name NVARCHAR(30),
    Order_price money

);

GO
EXEC sys.sp_cdc_enable_db
GO


GO
EXEC sys.sp_cdc_enable_table @source_schema = N'dbo',
                               @source_name   = N'TBL_Users',
                               @role_name = NULL,
                               @supports_net_changes = 0;
GO

GO
EXEC sys.sp_cdc_enable_table @source_schema = N'dbo',
                               @source_name   = N'TBL_Order',
                               @role_name = NULL,
                               @supports_net_changes = 0;
GO


INSERT  INTO TBL_Users VALUES('Ahmet Furkan','DEMIR','552');
INSERT  INTO TBL_Users VALUES('Mustafa','Kalemci','554');

INSERT  INTO TBL_Order VALUES(1,'Pizza',200);
INSERT  INTO TBL_Order VALUES(1,'Kulaklik',600);

