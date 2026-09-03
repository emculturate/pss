select  
<School ID> as School_ID,
<PA File Name> as File_Name, -- Example '6488_029361_IPxx_03.txt'
'F' as type, 
coalesce(try_to_date(YV.<Update Time>), try_to_date(YV.<Creation Time>)) as details_last_updated_date, 
--YV.<Birthdate> as birth_date,
right(YV.<Birthdate>,2) as birth_day, 
split_part(YV.<Birthdate>,'-',2) as birth_month, 
left(YV.<Birthdate>,4) as birth_year, 
YV.<City> as city, 
YV.<Country> as country, 
YV.<Email> as email_address, 
YV.<Enroll Term> as entry_term, 
YV.<Enroll Year> as entry_year, 
YV.<First Name> as first_name, 
YV.<Gender> as gender, 
YV.<Experience Name> as filler_field_5_length_35, 
YV.<Last Name> as last_name, 
YV.<Major> as major_interest, 
YV.<Original Visitor Type> as filler_field_6_length_35, 
YV.<Postal Code> as zip_code, 
YV.<School CEEB Code> as high_school_ceeb_code, 
YV.<State> as state, 
YV.<Street> as address_line_1, 
YV.<ID> as filler_field_2_length_35, 
null as act_composite_score, 
null as address_line_2, 
null as admit_status, 
null as application_method, 
null as application_status, 
null as application_submission_date, 
null as application_type, 
null as campus_visit_status, 
null as counselor_email, 
null as deposit_status, 
null as eab_student_id, 
null as enrollment_status, 
null as ethnicity, 
null as filler_field_1_length_3, 
null as filler_field_4_length_3, 
null as filler_field_7_length_3, 
null as filler_field_8_length_35, 
null as first_source_date, 
null as foreign_postal_code, 
null as grad_year, 
null as high_school_gpa, 
null as interest_rating, 
null as middle_name, 
null as opt_out_indicator, 
null as parent_email, 
null as phone_number, 
null as recruitment_or_enrollment_status, 
null as sat_critical_reading_score, 
null as sat_math_score, 
null as sat_writing_score, 
null as source_country_name, 
null as source_gender, 
null as source_state, 
null as status, 
null as zip_code_extended_4
from <[YouVisit].[Inquiry]> YV
where
-- International INQ Where Condition
(
   ( 
	 YV.<First Name> Not Like '*@*' 
         And YV.<First Name> Not Like '*/*' 
         And YV.<First Name> Not Like '*\\*' 
         And YV.<First Name> Not Like '*:*' 
         And YV.<First Name> Not Like '*.*' 
         And YV.<First Name> Not Like '*http*' 
         And YV.<First Name> Not Like '*;*' 
         And YV.<First Name> Not Like '*{*' 
         And YV.<First Name> Not Like '*}*'
   ) 
   AND (YV.<Last Name> Not Like '*@*' 
         And YV.<Last Name> Not Like '*/*' 
         And YV.<Last Name> Not Like '*\\*' 
         And YV.<Last Name> Not Like '*.*' 
         And YV.<Last Name> Not Like '*http*'
         And YV.<Last Name> Not Like '*;*' 
         And YV.<Last Name> Not Like '*{*' 
         And YV.<Last Name> Not Like '*}*'
   ) 
   AND Len(YV.<First Name>) > 1
   AND Len(YV.<Last Name>) > 1 
  AND (coalesce(YV.<Enroll Year>,YV.<Graduation Year>) = '2021')
  AND (
	--<Original Visitor Condition 1>
	  ( YV.<Country> Not In ('','USA','United States') 
        AND     YV.<Original Visitor Type> In ('hs_student','INCOMING FRESHMAN', 'junior','prospective_student','prospective_students','senior','hs_grad','Freshman',
											   'freshmean','cust_fresh','custom_senior','hs_student_non_ohio','high_school', 
                                     'hs_graduate','in High School','student','STUDENT','visitor','High School Student','High School Graduate','freshman_student','in High School','prospective_students', 
									'prospective_student','prospective', 
                                      'prospective student','custom_jr','custom_undergrad','Undergraduate Degree','hs_junior_soph','hs_senior','High School Student / Graduate','First-time Freshman',
                                      'Non OH high school student','prospective_freshman','Higg School Graduated','High School Senior','high_school_2013','high school junior','custom_fresh','freshman_stu','hs_student',
                                      'Junior in High School','junior in HS','Tenth Grader','Upcoming Freshman','Sophomore')
	 ) OR (
	   YV.<Original Visitor Type> In ('International Student','International Students','intl_student','intl_students','International','Int\'l Student','custom_Intl','InternatÆl Student'
									 )
	 )
   ) 
)