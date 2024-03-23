package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.reeler.ReelerRequest;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.repository.*;
import com.sericulture.registration.service.ReelerService;
import com.sericulture.registration.service.ReelerVirtualBankAccountService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
@RestController
@RequestMapping("/v1/reelerTemplate")
public class ReelerTemplateController {

        @Autowired
        ReelerRepository reelerRepository;
        @Autowired
        MachineTypeRepository machineTypeRepository;
        @Autowired
        StateRepository stateRepository;
        @Autowired
        VillageRepository villageRepository;
        @Autowired
        DistrictRepository districtRepository;
        @Autowired
        TalukRepository talukRepository;
        @Autowired
        HobliRepository hobliRepository;
        @Autowired
        CasteRepository casteRepository;
        @Autowired
        MarketMasterRepository marketMasterRepository;
        @Autowired
        ReelerTypeRepository reelerTypeRepository;

        @Autowired
        ReelerVirtualBankAccountService reelerVirtualBankAccountService;

        @Autowired
        ReelerVirtualBankAccountRepository reelerVirtualBankAccountRepository;

        @Autowired
        ReelerService  reelerService;

        @PostMapping("/reeler-template-data")
        public String readExcelData(@RequestParam MultipartFile file1) throws Exception{

            XSSFWorkbook workbook = new XSSFWorkbook(file1.getInputStream());

            // Getting the Sheet at index i
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("=> " + sheet.getSheetName());
            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();
            // 1. You can obtain a rowIterator and columnIterator and iterate over themreefarmer
            System.out.println("Iterating over Rows and Columns using Iterator");
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                ReelerRequest reeler = new ReelerRequest();
                ReelerVirtualBankAccountRequest reelerVirtualBankAccount= new ReelerVirtualBankAccountRequest();
//                District updateDistrict = new District();
//                Taluk updateTaluk = new Taluk();
//                Hobli updateHobli = new Hobli();
//                Village updateVillage = new Village();
                Row row = rowIterator.next();
                // Get the row number
                int rowNumber = row.getRowNum();
                // Now let's iterate over the columns of the current row
                if(rowNumber>0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        int cellIndex = cell.getColumnIndex();
                        String cellValue = dataFormatter.formatCellValue(cell);

                        switch (cellIndex) {

                            case 0:
                                //Fruits Id
                                System.out.print("fruitsId:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setFruitsId(cellValue);
                                }
                                break;

                            case 1:
                                //Reeler Name
                                System.out.print("reelerName:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setReelerName(cellValue);

                                }
                                break;

                            case 2:
                                //Passbook Number
                                System.out.print("passbookNumber:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setPassbookNumber(cellValue);

                                }
                                break;

                            case 3:
                                //Father Name
                                System.out.print("FatherName:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setFatherName(cellValue);

                                }
                                break;

                            case 4:
                                //Machine Type
                                System.out.print("machineType:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        MachineType machineType = machineTypeRepository.findByMachineTypeNameAndActive(cellValue,true);
                                        if(machineType!= null) {
                                            reeler.setMachineTypeId(machineType.getMachineTypeId());
                                        }
                                }
                                break;


                            case 5:
                                //Caste
                                System.out.print("caste:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    Caste caste = casteRepository.findByTitleAndActive(cellValue,true);
                                    if(caste!= null) {
                                        reeler.setCasteId(caste.getCasteId());
                                    }
                                }
                                break;

                            case 6:
                                //Reeler Number
                                System.out.print("reelerNumber:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setReelerNumber(cellValue);
                                }
                                break;

                            case 7:
                                //Reeler Type
                                System.out.print("reelerType:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    ReelerTypeMaster reelerTypeMaster = reelerTypeRepository.findByReelerTypeMasterNameAndActive(cellValue,true);
                                    if(reelerTypeMaster!= null) {
                                        reeler.setReelerTypeMasterId(reelerTypeMaster.getReelerTypeMasterId());
                                    }

                                }
                                break;


                            case 8:
                                //Mobile Number
                                System.out.print("mobileNumber:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setMobileNumber(cellValue);

                                }
                                break;

                            case 9:
                                //Address State
                                System.out.print("State:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    State state = stateRepository.findByStateNameAndActive(cellValue,true);
                                    if(state!= null) {
                                        reeler.setStateId(state.getStateId());
                                    }
                                }
                                break;


                            case 10:
                                //District
                                System.out.print("district:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    District district = districtRepository.findByDistrictNameAndActive(cellValue,true);
                                    if(district!= null) {
                                        reeler.setDistrictId(district.getDistrictId());
                                    }
                                }
                                break;

                            case 11:
                                //Taluk
                                System.out.print("taluk:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    Taluk taluk = talukRepository.findByTalukNameAndActive(cellValue,true);
                                    if(taluk!= null) {
                                        reeler.setTalukId(taluk.getTalukId());
                                    }
                                }
                                break;


                            case 12:
                                //Hobli
                                System.out.print("hobli:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    Hobli hobli = hobliRepository.findByHobliNameAndActive(cellValue,true);
                                    if(hobli!= null) {
                                        reeler.setHobliId(hobli.getHobliId());
                                    }
                                }
                                break;

                            case 13:
                                //Village
                                System.out.print("village:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    Village village = villageRepository.findByVillageNameAndActive(cellValue,true);
                                    if(village!= null) {
                                        reeler.setVillageId(village.getVillageId());
                                    }
                                }
                                break;

                            case 14:
                                //Pincode
                                System.out.print("village:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setPincode(cellValue);

                                }
                                break;

                            case 15:
                                //Address
                                System.out.print("village:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setAddress(cellValue);
                                }
                                break;

                            case 16:
                                //License
                                System.out.print("village:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setLicenseReceiptNumber(cellValue);
                                }
                                break;

                            case 17:
                                //Reeling Lisence Number
                                System.out.print("reelingLisenceNumber:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setReelingLicenseNumber(cellValue);

                                }
                                break;

                            case 18:
                                //Chakbandi Details East
                                System.out.print("East:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setMahajarEast(cellValue);
                                }
                                break;

                            case 19:
                                //West
                                System.out.print("West:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setMahajarWest(cellValue);
                                }
                                break;

                            case 20:
                                //North
                                System.out.print("north:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setMahajarNorth(cellValue);

                                }
                                break;

                            case 21:
                                //South
                                System.out.print("South:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setMahajarSouth(cellValue);

                                }
                                break;

                            case 22:
                                //Bank Name
                                System.out.print("bank:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setBankName(cellValue);
                                }
                                break;

                            case 23:
                                //Bank Account No
                                System.out.print("Bank Account No:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setBankAccountNumber(cellValue);
                                }
                                break;

                            case 24:
                                //Branch Name
                                System.out.print("Branch Name:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setBranchName(cellValue);
                                }
                                break;

                            case 25:
                                //Ifsc Code
                                System.out.print("ifsc code:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                        reeler.setIfscCode(cellValue);
                                }
                                break;


                            case 26:
                                //Virtual Bank Acc
                                System.out.print("virtual Bank Acc:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    reelerVirtualBankAccount.setVirtualAccountNumber(cellValue);

                                }
                                break;

                            case 27:
                                //Ifsc Code
                                System.out.print("ifsc code:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    reelerVirtualBankAccount.setIfscCode(cellValue);
                                }
                                break;

                            case 28:
                                //Branch Name
                                System.out.print("Branch Name:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    reelerVirtualBankAccount.setBranchName(cellValue);
                                }
                                break;


                            case 29:
                                //Market
                                System.out.print("market:" +cellValue + "\t");
                                if(!cellValue.equals("") && cellValue != null) {
                                    MarketMaster marketMaster = marketMasterRepository.findByMarketMasterNameAndActive(cellValue,true);
                                    if(marketMaster != null) {
                                        reelerVirtualBankAccount.setMarketMasterId(marketMaster.getMarketMasterId());
                                    }
                                }
                                break;



                        }
                    }

                    // Check if this is the last cell in the row
                    if (row.getLastCellNum() > 0 && row.getLastCellNum() == row.getPhysicalNumberOfCells()) {
                        System.out.println("\nEnd of Row " + rowNumber);
                    }

                    ReelerResponse savedReelerResponse = reelerService.insertReelerDetails(reeler);

                    if(savedReelerResponse.getError()== false) {
                        reelerVirtualBankAccount.setReelerId(savedReelerResponse.getReelerId());
                        ReelerVirtualBankAccountResponse savedReelerVirtualBankAccountResponse= reelerVirtualBankAccountService.insertReelerVirtualBankAccountDetails(reelerVirtualBankAccount);

                    }
                    //reeler save
                    //if(reeler!=null) - > ReelerId
                }
                System.out.println();
            }

            return "OK";
        }
}
