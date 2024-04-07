# THE INSURANCE CLAIM MANAGEMENT SYSTEM

## Overview
The insurance claims management system is structured to accommodate intricate details relating to customers, their insurance cards, and associated claims. Each customer, identifiable by a unique ID, can either be a policyholder or a dependent, holding an insurance card with specific details like the card number, holder, policy owner, and expiration date. Claims are meticulously recorded, including information such as the claim ID, dates, insured person, card number, associated documents, amount, status, and receiver banking information.

## Features
List the key features of this claim management system.
- View all feature
- View detail 
- Add new 
- Delete
- Edit
- Sorting
- Export to file 

## Getting Started

### Prerequisites
JDK version 21+

### Installation
1. Clone the repository: https://github.com/viethoang204/claim-management-system
2. Navigate into the directory: `cd claim-management-system`
3. Compile the Java files: `javac Main.java`
4. Run the application: `java Main.java`

## Demo Usage
### Main Menu
```bash
COSC2440 - Further Programming Assignment 
Name: Duong Viet Hoang
Student ID: s3962514
Instructor: Mr. Minh Vu Thanh & Mr. Tuan Phong Ngo

====================================================================
========= WELCOME TO THE INSURANCE CLAIM MANAGEMENT SYSTEM =========
====================================================================

============================ HOME MENU =============================
1. Claim Manager
2. Customer Manager
3. Insurance Card Manager
4. Exit
Enter your choice: 
```

### Claim Management Menu
```bash
======================== CLAIM MANAGER MENU ========================
1. View All Claim
2. Add Claim
3. Remove Claim
4. Edit Claim
5. Save As File
6. Return
Enter your choice:
```

### View All Feature
```bash
============================ VIEW CLAIM ==============================
 ID            | Claim Date  | Insured Person     | Card Number  | Exam Date   | List of Documents                                                       | Claim Amount  | Status      | Receiver Banking Info                        |
 f-0000000001  | 05-04-2024  | Duong Xuan Sanh    | 3268714742   | 07-04-2024  | f-0000000001_3268714742_report.pdf                                      | 500           | New         | VIETCOMBANK-DUONG XUAN SANH-793284102394     |
 f-0000000002  | 02-10-2019  | Duong Xuan Sanh    | 3268714742   | 02-11-2019  | f-0000000002_3268714742_health_status.pdf                               | 200           | Done        | VIETCOMBANK-DUONG XUAN SANH-793284102394     |
 f-0000000003  | 10-03-2024  | Duong Viet Hoang   | 1140196232   | 15-03-2024  | f-0000000003_1140196232_image1.pdf, f-0000000003_1140196232_image2.pdf  | 300           | Processing  | TECHCOMBANK-DUONG VIET HOANG-901238579231    |
 f-0000000004  | 25-03-2024  | Duong Hoang Viet   | 4815064308   | 30-03-2024  |                                                                         | 1500          | New         | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000005  | 20-09-2029  | Nguyen Quang Minh  | 8963572903   | 30-09-2019  | f-0000000005_8963572903_diagnosis.pdf                                   | 500           | Done        | SACOMBANK-NGUYEN QUANG MINH-1231239108734    |
 f-0000000006  | 23-10-2020  | Doan Thu Trang     | 3249875849   | 25-10-2020  | f-0000000006_3249875849_accident_infor.pdf                              | 50            | Done        | MSBBANK-DOAN THU TRANG-9045686723465         |
 f-0000000007  | 20-10-2022  | Nguyen Nhat Long   | 6728362155   | 23-10-2022  | f-0000000007_6728362155_trip_detail.pdf                                 | 1000          | Done        | VIETTINBANK-NGUYEN NHAT LONG-2348235975522   |
 f-0000000008  | 01-04-2024  | Nguyen Minh Khue   | 5615889493   | 03-04-2024  | f-0000000008_5615889493_report1.pdf                                     | 10000         | Processing  | HSBC-NGUYEN MINH KHUE-23478752934213         |
 f-0000000009  | 04-04-2024  | Nguyen Nhan The    | 2636384770   | 10-04-2024  | f-0000000009_2636384770_repair_estimates.pdf                            | 250           | New         | TECHCOMBANK-NGUYEN NHAN THE-634028137421051  |
 f-0000000010  | 04-04-2024  | Duong Hoang Viet   | 4815064308   | 15-04-2024  | f-0000000010_4815064308_diagnosis.pdf                                   | 550           | New         | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000011  | 11-11-2018  | Duong Hoang Viet   | 4815064308   | 20-11-2018  | f-0000000011_4815064308_proof.pdf                                       | 980           | Done        | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000012  | 30-04-2023  | Nguyen Mai Ngoc    | 3771982908   | 30-04-2023  | f-0000000012_3771982908_provider.pdf                                    | 2000          | Done        | TPBANK-NGUYEN MAI NGOC-2389472380957         |
 f-0000000013  | 14-01-2024  | Nguyen Thai Son    | 0785306606   | 18-01-2924  |                                                                         | 50            | Processing  | BIDV-NGUYEN THAI SON-340659832               |
 f-0000000014  | 19-06-2023  | Duong Hoang Viet   | 4815064308   | 20-07-2023  |                                                                         | 1200          | Processing  | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000015  | 30-03-2024  | Duong Hoang Viet   | 4815064308   | 04-04-2024  | f-0000000015_4815064308_inventory_of_damaged_items.pdf                  | 850           | Processing  | MSBBANK-DUONG HOANG VIET-12301923810294      |
======================================================================
1. View Detail Of A Claim
2. Sorting
3. Return
Enter your choice: 
```

### View Detail A Claim
```bash
———————————————————————————— CLAIM DETAIL ———————————————————————————
ID: f-0000000001
Claim Date: 05-04-2024
Insured Person: Duong Xuan Sanh
Card Number: 3268714742
Exam Date: 07-04-2024
List of Documents: f-0000000001_3268714742_report.pdf
Claim Amount($): 500.0
Status: New
Receiver Banking Info: VIETCOMBANK-DUONG XUAN SANH-793284102394

—————— INSURED PERSON DETAIL OF DUONG XUAN SANH ——————
 ID         | Full Name        | Title          |
 c-0000001  | Duong Xuan Sanh  | Policy Holder  |

—————— INSURANCE CARD DETAIL OF DUONG XUAN SANH ——————
 Card Number  | Card Holder      | Policy Owner  | Expiration Date  |
 3268714742   | Duong Xuan Sanh  | VKS           | 30-11-2030       |
```

### Sorting Claim By Claim Amount From Lowest To Highest
```bash
============================ VIEW CLAIM ==============================
 ID            | Claim Date  | Insured Person     | Card Number  | Exam Date   | List of Documents                                                       | Claim Amount  | Status      | Receiver Banking Info                        |
 f-0000000006  | 23-10-2020  | Doan Thu Trang     | 3249875849   | 25-10-2020  | f-0000000006_3249875849_accident_infor.pdf                              | 50            | Done        | MSBBANK-DOAN THU TRANG-9045686723465         |
 f-0000000013  | 14-01-2024  | Nguyen Thai Son    | 0785306606   | 18-01-2924  |                                                                         | 50            | Processing  | BIDV-NGUYEN THAI SON-340659832               |
 f-0000000002  | 02-10-2019  | Duong Xuan Sanh    | 3268714742   | 02-11-2019  | f-0000000002_3268714742_health_status.pdf                               | 200           | Done        | VIETCOMBANK-DUONG XUAN SANH-793284102394     |
 f-0000000009  | 04-04-2024  | Nguyen Nhan The    | 2636384770   | 10-04-2024  | f-0000000009_2636384770_repair_estimates.pdf                            | 250           | New         | TECHCOMBANK-NGUYEN NHAN THE-634028137421051  |
 f-0000000003  | 10-03-2024  | Duong Viet Hoang   | 1140196232   | 15-03-2024  | f-0000000003_1140196232_image1.pdf, f-0000000003_1140196232_image2.pdf  | 300           | Processing  | TECHCOMBANK-DUONG VIET HOANG-901238579231    |
 f-0000000001  | 05-04-2024  | Duong Xuan Sanh    | 3268714742   | 07-04-2024  | f-0000000001_3268714742_report.pdf                                      | 500           | New         | VIETCOMBANK-DUONG XUAN SANH-793284102394     |
 f-0000000005  | 20-09-2029  | Nguyen Quang Minh  | 8963572903   | 30-09-2019  | f-0000000005_8963572903_diagnosis.pdf                                   | 500           | Done        | SACOMBANK-NGUYEN QUANG MINH-1231239108734    |
 f-0000000010  | 04-04-2024  | Duong Hoang Viet   | 4815064308   | 15-04-2024  | f-0000000010_4815064308_diagnosis.pdf                                   | 550           | New         | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000015  | 30-03-2024  | Duong Hoang Viet   | 4815064308   | 04-04-2024  | f-0000000015_4815064308_inventory_of_damaged_items.pdf                  | 850           | Processing  | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000011  | 11-11-2018  | Duong Hoang Viet   | 4815064308   | 20-11-2018  | f-0000000011_4815064308_proof.pdf                                       | 980           | Done        | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000007  | 20-10-2022  | Nguyen Nhat Long   | 6728362155   | 23-10-2022  | f-0000000007_6728362155_trip_detail.pdf                                 | 1000          | Done        | VIETTINBANK-NGUYEN NHAT LONG-2348235975522   |
 f-0000000014  | 19-06-2023  | Duong Hoang Viet   | 4815064308   | 20-07-2023  |                                                                         | 1200          | Processing  | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000004  | 25-03-2024  | Duong Hoang Viet   | 4815064308   | 30-03-2024  |                                                                         | 1500          | New         | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000012  | 30-04-2023  | Nguyen Mai Ngoc    | 3771982908   | 30-04-2023  | f-0000000012_3771982908_provider.pdf                                    | 2000          | Done        | TPBANK-NGUYEN MAI NGOC-2389472380957         |
 f-0000000008  | 01-04-2024  | Nguyen Minh Khue   | 5615889493   | 03-04-2024  | f-0000000008_5615889493_report1.pdf                                     | 10000         | Processing  | HSBC-NGUYEN MINH KHUE-23478752934213         |
======================================================================
```

### Save Process Demo
```bash
=========================== SAVING CLAIM ============================
—————————————————————— PREVIEW THE CLAIM LIST ———————————————————————
 ID            | Claim Date  | Insured Person     | Card Number  | Exam Date   | List of Documents                                                       | Claim Amount  | Status      | Receiver Banking Info                        |
 f-0000000006  | 23-10-2020  | Doan Thu Trang     | 3249875849   | 25-10-2020  | f-0000000006_3249875849_accident_infor.pdf                              | 50            | Done        | MSBBANK-DOAN THU TRANG-9045686723465         |
 f-0000000013  | 14-01-2024  | Nguyen Thai Son    | 0785306606   | 18-01-2924  |                                                                         | 50            | Processing  | BIDV-NGUYEN THAI SON-340659832               |
 f-0000000002  | 02-10-2019  | Duong Xuan Sanh    | 3268714742   | 02-11-2019  | f-0000000002_3268714742_health_status.pdf                               | 200           | Done        | VIETCOMBANK-DUONG XUAN SANH-793284102394     |
 f-0000000009  | 04-04-2024  | Nguyen Nhan The    | 2636384770   | 10-04-2024  | f-0000000009_2636384770_repair_estimates.pdf                            | 250           | New         | TECHCOMBANK-NGUYEN NHAN THE-634028137421051  |
 f-0000000003  | 10-03-2024  | Duong Viet Hoang   | 1140196232   | 15-03-2024  | f-0000000003_1140196232_image1.pdf, f-0000000003_1140196232_image2.pdf  | 300           | Processing  | TECHCOMBANK-DUONG VIET HOANG-901238579231    |
 f-0000000001  | 05-04-2024  | Duong Xuan Sanh    | 3268714742   | 07-04-2024  | f-0000000001_3268714742_report.pdf                                      | 500           | New         | VIETCOMBANK-DUONG XUAN SANH-793284102394     |
 f-0000000005  | 20-09-2029  | Nguyen Quang Minh  | 8963572903   | 30-09-2019  | f-0000000005_8963572903_diagnosis.pdf                                   | 500           | Done        | SACOMBANK-NGUYEN QUANG MINH-1231239108734    |
 f-0000000010  | 04-04-2024  | Duong Hoang Viet   | 4815064308   | 15-04-2024  | f-0000000010_4815064308_diagnosis.pdf                                   | 550           | New         | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000015  | 30-03-2024  | Duong Hoang Viet   | 4815064308   | 04-04-2024  | f-0000000015_4815064308_inventory_of_damaged_items.pdf                  | 850           | Processing  | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000011  | 11-11-2018  | Duong Hoang Viet   | 4815064308   | 20-11-2018  | f-0000000011_4815064308_proof.pdf                                       | 980           | Done        | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000007  | 20-10-2022  | Nguyen Nhat Long   | 6728362155   | 23-10-2022  | f-0000000007_6728362155_trip_detail.pdf                                 | 1000          | Done        | VIETTINBANK-NGUYEN NHAT LONG-2348235975522   |
 f-0000000014  | 19-06-2023  | Duong Hoang Viet   | 4815064308   | 20-07-2023  |                                                                         | 1200          | Processing  | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000004  | 25-03-2024  | Duong Hoang Viet   | 4815064308   | 30-03-2024  |                                                                         | 1500          | New         | MSBBANK-DUONG HOANG VIET-12301923810294      |
 f-0000000012  | 30-04-2023  | Nguyen Mai Ngoc    | 3771982908   | 30-04-2023  | f-0000000012_3771982908_provider.pdf                                    | 2000          | Done        | TPBANK-NGUYEN MAI NGOC-2389472380957         |
 f-0000000008  | 01-04-2024  | Nguyen Minh Khue   | 5615889493   | 03-04-2024  | f-0000000008_5615889493_report1.pdf                                     | 10000         | Processing  | HSBC-NGUYEN MINH KHUE-23478752934213         |
—————————————————————————————————————————————————————————————————————
The claim table is currently sorted by the claim amount from lowest oldest to highest order
Would you like to change the order before saving the file?
1. Yes. moving to sorting menu
2. No, save the file
3. Return
Enter your choice: 2
1. Save as table format TXT
2. Save as TSV
3. Save as CSV
4. Return
Enter your choice: 2
Enter file name to save as TSV: demo
File saved successfully at savedFile/demo.tsv
```