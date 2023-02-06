
use cst363;

insert into drug (drugId, tradeName, genericName) values
	( 1, "trade1", "generic1" ),
	( 2, "trade2", "generic2" ),
	( 3, "trade3", "generic3" ),
	( 4, "trade4", "generic4" ),
	( 5, "trade5", "generic5" ),
	( 6,     null, "generic1" ),
	( 7,     null, "generic2" ),
	( 8,     null, "generic3" ),
	( 9,     null, "generic4" );

insert into pharmacy (pharmacyId, pharmacyName, pharmacyPhone, pharmacyZip, pharmacyCity, pharmacyStreet) values
	( 1, "pharmacy1", "555-123-4567", "12345", "city1", "street2" ),
	( 2, "pharmacy2", "555-234-5678", "23456", "city1", "street1" ),
	( 3, "pharmacy3", "555-345-6789", "34567", "city2", "street1" );

insert into pharmacyDrug (pharmacyId, drugId, price) values
	( 1, 1,  10 ),
	( 1, 2,  11 ),
	( 1, 3,  12 ),
	( 2, 4,  10 ),
	( 2, 5,  11 ),
	( 2, 6,  12 ),
	( 3, 1, 100 ),
	( 3, 2, 101 ),
	( 3, 3, 102 ),
	( 3, 4, 103 ),
	( 3, 5, 105 ),
	( 3, 6, 105 ),
	( 3, 7, 106 ),
	( 3, 8, 107 ),
	( 3, 9, 108 );

insert into doctor (doctorId, doctorSSN, doctorFirstName, doctorLastName, specialty, practiceSinceYear) values
	( 1, "012345678",   "Joe", "Reeves",   "Family Medicine", 2000 ),
	( 2, "123456789",   "Joe",  "Schmo", "Internal Medicine", 2000 ),
	( 3, "234567890", "Schmo", "Reeves",        "Pediatrics", 2001 );

insert into patient (patientId, primaryDoctorId, patientSSN, patientFirstName, patientLastName, patientBirthdate, patientState, patientZip, patientCity, patientStreet) values
	( 1, 1, "901234567", "John",     "Doe", "1999-01-01", "California", "01234", "city1", "street4" ),
	( 2, 1, "890123456", "John", "Johnson", "2000-02-01",   "Arkansas", "98765", "city2", "street4" ),
	( 3, 2, "789012345",  "Doe", "Johnson", "2000-02-01", "California", "01234", "city1", "street5" );

insert into prescription
	(rxNum, doctorId, patientId, drugId, prescribeDate, quantity) values
	(  1,       1,        1,        1,    "2020-01-01",    1    ), -- "Joe Reeves"  prescribes "John Doe"      1 of the drug ( "trade1", "generic1" )
	(  2,       2,        1,        1,    "2020-02-01",    1    ), -- "Joe Schmo"   prescribes "John Doe"      1 of the drug ( "trade1", "generic1" )
	(  3,       2,        2,        1,    "2020-01-01",    2    ), -- "Joe Schmo"   prescribes "John Johnson"  2 of the drug ( "trade1", "generic1" )
	(  4,       3,        3,        1,    "2020-01-01",    1    ), -- "Doe Johnson" prescribes "Schmo Reeves"  1 of the drug ( "trade1", "generic1" )
	(  5,       3,        3,        2,    "2020-02-01",    2    ), -- "Doe Johnson" prescribes "Schmo Reeves"  2 of the drug ( "trade2", "generic2" )
	(  6,       3,        3,        3,    "2020-03-01",    1    ), -- "Doe Johnson" prescribes "Schmo Reeves"  1 of the drug ( "trade3", "generic3" )
	(  7,       3,        3,        4,    "2020-04-01",    3    ), -- "Doe Johnson" prescribes "Schmo Reeves"  3 of the drug ( "trade4", "generic3" )
	(  8,       3,        3,        5,    "2020-05-01",    4    ), -- "Doe Johnson" prescribes "Schmo Reeves"  4 of the drug ( "trade5", "generic4" )
	(  9,       3,        3,        6,    "2020-06-01",    3    ), -- "Doe Johnson" prescribes "Schmo Reeves"  3 of the drug (     null, "generic1" )
	( 10,       3,        3,        7,    "2020-07-01",    1    ), -- "Doe Johnson" prescribes "Schmo Reeves"  1 of the drug (     null, "generic2" )
	( 11,       3,        3,        8,    "2020-08-01",   20    ), -- "Doe Johnson" prescribes "Schmo Reeves" 20 of the drug (     null, "generic3" )
	( 12,       3,        3,        9,    "2020-09-01",   10    ); -- "Doe Johnson" prescribes "Schmo Reeves" 10 of the drug (     null, "generic4" )

insert into company (companyId, companyName, companyPhone) values
	( 1, "company1", "999-123-4567" ),
	( 2, "company2", "999-234-5678" ),
	( 3, "company3", "999-456-7890" );

insert into companyMakesDrug (companyId, drugId) values
	( 1, 1 ), -- "company1" makes ( "trade1", "generic1" )
	( 1, 2 ), -- "company1" makes ( "trade2", "generic2" )
	( 1, 6 ), -- "company1" makes (     null, "generic1" )
	( 2, 3 ), -- "company2" makes ( "trade3", "generic3" )
	( 2, 4 ), -- "company2" makes ( "trade4", "generic4" )
	( 2, 5 ), -- "company2" makes ( "trade5", "generic5" )
	( 2, 6 ), -- "company2" makes (     null, "generic1" )
	( 2, 7 ), -- "company2" makes (     null, "generic2" )
	( 3, 6 ), -- "company3" makes (     null, "generic1" )
	( 3, 7 ), -- "company3" makes (     null, "generic2" )
	( 3, 8 ), -- "company3" makes (     null, "generic3" )
	( 3, 9 ); -- "company3" makes (     null, "generic4" )

insert into fill (fillId, rxNum, pharmacyId, fillDate, fillDrugId, fillGenericCompanyId) values
	( 1, 1,  1, "2020-01-09", 1, null ), -- prescription 1 was filled by "pharmacy1" with drug made by "company1"
	( 2, 1,  1, "2020-01-16", 1, null ), -- prescription 1 was filled by "pharmacy1" with drug made by "company1"
	( 3, 1,  2, "2020-01-09", 1, null ), -- prescription 1 was filled by "pharmacy2" with drug made by "company1"
	( 4, 4,  2, "2020-01-09", 1, null ), -- prescription 4 was filled by "pharmacy2" with drug made by "company1"
	( 5, 9,  3, "2020-01-09", 1, null ), -- prescription 9 was filled by "pharmacy3" with drug made by "company1"
	( 6, 9,  3, "2020-01-16", 6, 1    ), -- prescription 9 was filled by "pharmacy3" with drug made by "company1" (generic)
	( 7, 9,  3, "2020-01-23", 6, 2    ), -- prescription 9 was filled by "pharmacy3" with drug made by "company2" (generic)
	( 8, 9,  3, "2020-01-30", 6, 3    ); -- prescription 9 was filled by "pharmacy3" with drug made by "company3" (generic)
