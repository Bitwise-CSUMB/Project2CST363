
use cst363;

insert into drug (drugId, tradeName, genericName) values
	( 1,   "Trade One", "Generic One"   ),
	( 2,   "Trade Two", "Generic Two"   ),
	( 3, "Trade Three", "Generic Three" ),
	( 4,  "Trade Four", "Generic Four"  ),
	( 5,  "Trade Five", "Generic Five"  ),
	( 6,          null, "Generic One"   ),
	( 7,          null, "Generic Two"   ),
	( 8,          null, "Generic Three" ),
	( 9,          null, "Generic Four"  );

insert into pharmacy (pharmacyId, pharmacyName, pharmacyPhone, pharmacyZip, pharmacyCity, pharmacyStreet) values
	( 1,   "Pharmacy One", "555-123-4567", "12345", "City One", "Street Two" ),
	( 2,   "Pharmacy Two", "555-234-5678", "23456", "City One", "Street One" ),
	( 3, "Pharmacy Three", "555-345-6789", "34567", "City Two", "Street One" );

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
	(  1, "112345678",           "Joe",  "Reeves",   "Family Medicine", 2000 ),
	(  2, "123456789",           "Joe",   "Schmo", "Internal Medicine", 2000 ),
	(  3, "234567890",         "Schmo",  "Reeves",        "Pediatrics", 2001 ),
	(  4, "111111111",   "Docktor One", "Docktor",   "Family Medicine", 2000 ),
	(  5, "222222222",   "Docktor Two", "Docktor", "Internal Medicine", 2000 ),
	(  6, "333333333", "Docktor Three", "Docktor",        "Pediatrics", 2001 ),
	(  7, "444444444",  "Docktor Four", "Docktor",   "Family Medicine", 2000 ),
	(  8, "555555555",  "Docktor Five", "Docktor", "Internal Medicine", 2000 ),
	(  9, "666666666",   "Docktor Six", "Docktor",        "Pediatrics", 2001 ),
	( 10, "777777777", "Docktor Seven", "Docktor",   "Family Medicine", 2000 ),
	( 11, "888888888", "Docktor Eight", "Docktor", "Internal Medicine", 2000 );

insert into patient (patientId, primaryDoctorId, patientSSN, patientFirstName, patientLastName, patientBirthdate, patientState, patientZip, patientCity, patientStreet) values
	( 1, 1, "801234567", "John",     "Doe", "1999-01-01", "California", "01234", "City One", "Street Four" ),
	( 2, 1, "890123456", "John", "Johnson", "2000-02-01",   "Arkansas", "98765", "City Two", "Street Four" ),
	( 3, 2, "789012345",  "Doe", "Johnson", "2000-02-01", "California", "01234", "City One", "Street Five" );

insert into prescription
	(rxNum, doctorId, patientId, drugId, prescribeDate, quantity) values
	(  1,       1,        1,        1,    "2020-01-01",    1    ), -- "Joe Reeves"  prescribes "John Doe"      1 of the drug (   "Trade One", "Generic One"   )
	(  2,       2,        1,        1,    "2020-02-01",    1    ), -- "Joe Schmo"   prescribes "John Doe"      1 of the drug (   "Trade One", "Generic One"   )
	(  3,       2,        2,        1,    "2020-01-01",    2    ), -- "Joe Schmo"   prescribes "John Johnson"  2 of the drug (   "Trade One", "Generic One"   )
	(  4,       3,        3,        1,    "2020-01-01",    1    ), -- "Schmo Reeves" prescribes "Doe Johnson"  1 of the drug (   "Trade One", "Generic One"   )
	(  5,       3,        3,        2,    "2020-02-01",    2    ), -- "Schmo Reeves" prescribes "Doe Johnson"  2 of the drug (   "Trade Two", "Generic Two"   )
	(  6,       3,        3,        3,    "2020-03-01",    1    ), -- "Schmo Reeves" prescribes "Doe Johnson"  1 of the drug ( "Trade Three", "Generic Three" )
	(  7,       3,        3,        4,    "2020-04-01",    3    ), -- "Schmo Reeves" prescribes "Doe Johnson"  3 of the drug (  "Trade Four", "Generic Four"  )
	(  8,       3,        3,        5,    "2020-05-01",    4    ), -- "Schmo Reeves" prescribes "Doe Johnson"  4 of the drug (  "Trade Five", "Generic Five"  )
	(  9,       3,        3,        6,    "2020-06-01",    3    ), -- "Schmo Reeves" prescribes "Doe Johnson"  3 of the drug (          null, "Generic One"   )
	( 10,       3,        3,        7,    "2020-07-01",    1    ), -- "Schmo Reeves" prescribes "Doe Johnson"  1 of the drug (          null, "Generic Two"   )
	( 11,       3,        3,        8,    "2020-08-01",   20    ), -- "Schmo Reeves" prescribes "Doe Johnson" 20 of the drug (          null, "Generic Three" )
	( 12,       3,        3,        9,    "2020-09-01",   10    ); -- "Schmo Reeves" prescribes "Doe Johnson" 10 of the drug (          null, "Generic Four"  )

insert into company (companyId, companyName, companyPhone) values
	( 1,   "Company One", "999-123-4567" ),
	( 2,   "Company Two", "999-234-5678" ),
	( 3, "Company Three", "999-456-7890" );

insert into companyMakesDrug (companyId, drugId) values
	( 1, 1 ), -- "Company One" makes   (   "Trade One", "generic1" )
	( 1, 2 ), -- "Company One" makes   (   "Trade Two", "generic2" )
	( 1, 6 ), -- "Company One" makes   (          null, "generic1" )
	( 2, 3 ), -- "Company Two" makes   ( "Trade Three", "generic3" )
	( 2, 4 ), -- "Company Two" makes   (  "Trade Four", "generic4" )
	( 2, 5 ), -- "Company Two" makes   (  "Trade Five", "generic5" )
	( 2, 6 ), -- "Company Two" makes   (          null, "generic1" )
	( 2, 7 ), -- "Company Two" makes   (          null, "generic2" )
	( 3, 6 ), -- "Company Three" makes (          null, "generic1" )
	( 3, 7 ), -- "Company Three" makes (          null, "generic2" )
	( 3, 8 ), -- "Company Three" makes (          null, "generic3" )
	( 3, 9 ); -- "Company Three" makes (          null, "generic4" )

insert into fill (fillId, rxNum, pharmacyId, fillDate, fillDrugId, fillGenericCompanyId) values
	( 1, 1,  1, "2020-01-09", 1, null ), -- prescription 1 was filled by "Pharmacy One" with drug made by "Company One"
	( 2, 1,  1, "2020-01-16", 1, null ), -- prescription 1 was filled by "Pharmacy One" with drug made by "Company One"
	( 3, 1,  2, "2020-01-09", 1, null ), -- prescription 1 was filled by "Pharmacy Two" with drug made by "Company One"
	( 4, 4,  2, "2020-01-09", 1, null ), -- prescription 4 was filled by "Pharmacy Two" with drug made by "Company One"
	( 5, 9,  3, "2020-01-09", 1, null ), -- prescription 9 was filled by "Pharmacy Three" with drug made by "Company One"
	( 6, 9,  3, "2020-01-16", 6, 1    ), -- prescription 9 was filled by "Pharmacy Three" with drug made by "Company One" (generic)
	( 7, 9,  3, "2020-01-23", 6, 2    ), -- prescription 9 was filled by "Pharmacy Three" with drug made by "Company Two" (generic)
	( 8, 9,  3, "2020-01-30", 6, 3    ); -- prescription 9 was filled by "Pharmacy Three" with drug made by "Company Three" (generic)