package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.farmer.FarmerRequest;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.api.reeler.ReelerRequest;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.repository.*;
import com.sericulture.registration.service.*;
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
@RequestMapping("/v1/farmerTemplate")
public class FarmerTemplateController {


    @Autowired
    FarmerService farmerService;
    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    MulberrySourceRepository mulberrySourceRepository;

    @Autowired
    IrrigationTypeRepository irrigationTypeRepository;

    @Autowired
    FarmerLandDetailsService farmerLandDetailsService;

    @Autowired
    FarmerBankAccountService farmerBankAccountService;

    @Autowired
    IrrigationSourceRepository irrigationSourceRepository;

    @Autowired
    FarmerLandDetailsRepository farmerLandDetailsRepository;

    @Autowired
    PlantationTypeRepository plantationTypeRepository;

    @Autowired
    FarmerTypeRepository farmerTypeRepository;
    @Autowired
    MachineTypeRepository machineTypeRepository;

    @Autowired
    FarmerAddressService farmerAddressService;
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
    SilkWormVarietyRepository silkWormVarietyRepository;

    @Autowired
    LandOwnershipRepository landOwnershipRepository;


    @Autowired
    SoilTypeRepository soilTypeRepository;

    @Autowired
    RoofTypeRepository roofTypeRepository;
    @Autowired
    MulberryVarietyRepository mulberryVarietyRepository;

    @Autowired
    ReelerVirtualBankAccountService reelerVirtualBankAccountService;

    @Autowired
    ReelerVirtualBankAccountRepository reelerVirtualBankAccountRepository;


    @PostMapping("/farmer-template-data")
    public String readExcelData(@RequestParam MultipartFile file2) throws Exception{

        XSSFWorkbook workbook = new XSSFWorkbook(file2.getInputStream());

        // Getting the Sheet at index i
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println("=> " + sheet.getSheetName());
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        // 1. You can obtain a rowIterator and columnIterator and iterate over themreefarmer
        System.out.println("Iterating over Rows and Columns using Iterator");
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            FarmerRequest farmer = new FarmerRequest();
            FarmerLandDetailsRequest farmerLandDetails = new FarmerLandDetailsRequest();
            FarmerAddressRequest farmerAddress= new FarmerAddressRequest();
            FarmerBankAccountRequest farmerBankAccount = new FarmerBankAccountRequest();



//                District updateDistrict = new District();
//                Taluk updateTaluk = new Taluk();
//                Hobli updateHobli = new Hobli();
//                Village updateVillage = new Village();
            Row row = rowIterator.next();
            // Get the row number
            int rowNumber = row.getRowNum();
            // Now let's iterate over the columns of the current row
            try {
                if (rowNumber > 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        int cellIndex = cell.getColumnIndex();
                        String cellValue = dataFormatter.formatCellValue(cell);

                        switch (cellIndex) {

                            case 0:
                                //Fruits Id
                                System.out.print("fruitsId:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setFruitsId(cellValue);
                                }
                                break;

                            case 1:
                                //Farmer Name
                                System.out.print("farmerName:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setFirstName(cellValue);

                                }
                                break;

                            case 2:
                                //Name In Kannada
                                System.out.print("Name In Kannada:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setNameKan(cellValue);

                                }
                                break;

                            case 3:
                                //Mobile Number
                                System.out.print("Mobile Number:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setMobileNumber(cellValue);

                                }
                                break;

                            case 4:
                                //Farmer Number
                                System.out.print("Framer Number:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setFarmerNumber(cellValue);

                                }
                                break;

                            case 5:
                                //Father Name
                                System.out.print("Father Name:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setFatherName(cellValue);

                                }
                                break;

                            case 6:
                                //Father Name In Kannada
                                System.out.print("Father Name In Kannada:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setMobileNumber(cellValue);

                                }
                                break;

                            case 7:
                                //Extent Of Total Land In Acres
                                System.out.print("Total Land In Acres:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setTotalLandHolding(cellValue);

                                }
                                break;

                            case 8:
                                //Passbook Number
                                System.out.print("Passbook Number:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmer.setPassbookNumber(cellValue);

                                }
                                break;


                            case 9:
                                //Framer Type
                                System.out.print("Farmer Type:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    FarmerType farmerType = farmerTypeRepository.findByActiveAndFarmerTypeName(true, cellValue);
                                    if (farmerType != null) {
                                        farmer.setFarmerTypeId(farmerType.getFarmerTypeId());
                                    }
                                }
                                break;


                            case 10:
                                //Caste
                                System.out.print("caste:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    Caste caste = casteRepository.findByTitleAndActive(cellValue, true);
                                    if (caste != null) {
                                        farmer.setCasteId(caste.getCasteId());
                                    }
                                }
                                break;


                            case 11:
                                //Address State
                                System.out.print("State:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    State state = stateRepository.findByStateNameAndActive(cellValue, true);
                                    if (state != null) {
                                        farmerAddress.setStateId(state.getStateId());
                                    }
                                }
                                break;


                            case 12:
                                //District
                                System.out.print("district:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    District district = districtRepository.findByDistrictNameAndActive(cellValue, true);
                                    if (district != null) {
                                        farmerAddress.setDistrictId(district.getDistrictId());
                                    }
                                }
                                break;

                            case 13:
                                //Taluk
                                System.out.print("taluk:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    Taluk taluk = talukRepository.findByTalukNameAndActive(cellValue, true);
                                    if (taluk != null) {
                                        farmerAddress.setTalukId(taluk.getTalukId());
                                    }
                                }
                                break;


                            case 14:
                                //Hobli
                                System.out.print("hobli:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    Hobli hobli = hobliRepository.findByHobliNameAndActive(cellValue, true);
                                    if (hobli != null) {
                                        farmerAddress.setHobliId(hobli.getHobliId());
                                    }
                                }
                                break;

                            case 15:
                                //Village
                                System.out.print("village:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    Village village = villageRepository.findByVillageNameAndActive(cellValue, true);
                                    if (village != null) {
                                        farmerAddress.setVillageId(village.getVillageId());
                                    }
                                }
                                break;

                            case 16:
                                //Pincode
                                System.out.print("pincode:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerAddress.setPincode(cellValue);

                                }
                                break;

                            case 17:
                                //Address
                                System.out.print("village:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerAddress.setAddressText(cellValue);
                                }
                                break;

                            case 18:
                                //Farmer Land Details Ownership
                                System.out.print("Farmer Land Details Ownership:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    LandOwnership landOwnership = landOwnershipRepository.findByLandOwnershipNameAndActive(cellValue, true);
                                    if (landOwnership != null) {
                                        farmerLandDetails.setLandOwnershipId(landOwnership.getLandOwnershipId());
                                    }
                                }
                                break;

                            case 19:
                                //Plantation Type
                                System.out.print("Plantation Type:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    PlantationType plantationType = plantationTypeRepository.findByPlantationTypeNameAndActive(cellValue, true);
                                    if (plantationType != null) {
                                        farmerLandDetails.setPlantationTypeId(farmerLandDetails.getPlantationTypeId());
                                    }
                                }
                                break;

                            case 20:
                                //Soil Type
                                System.out.print("Soil Type:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    SoilType soilType = soilTypeRepository.findBySoilTypeNameAndActive(cellValue, true);
                                    if (soilType != null) {
                                        farmerLandDetails.setSoilTypeId(farmerLandDetails.getSoilTypeId());
                                    }
                                }
                                break;

                            case 21:
                                //Irrigation Source
                                System.out.print("Irrigation Source:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    IrrigationSource irrigationSource = irrigationSourceRepository.findByIrrigationSourceNameAndActive(cellValue, true);
                                    if (irrigationSource != null) {
                                        farmerLandDetails.setIrrigationSourceId(farmerLandDetails.getIrrigationSourceId());
                                    }
                                }
                                break;


                            case 22:
                                //Rearing Capacity Dlf
                                System.out.print("rearingCapacity:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerLandDetails.setRearingCapacityDlf(cellValue);

                                }
                                break;

                            case 23:
                                //Irrigation Type
                                System.out.print("Irrigation Type:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    IrrigationType irrigationType = irrigationTypeRepository.findByIrrigationTypeNameAndActive(cellValue, true);
                                    if (irrigationType != null) {
                                        farmerLandDetails.setIrrigationTypeId(farmerLandDetails.getIrrigationTypeId());
                                    }
                                }
                                break;

                            case 24:
                                //Source of Mulberry Fruits
                                System.out.print("Source Of Mulberry Fruits:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    MulberrySource mulberrySource = mulberrySourceRepository.findByMulberrySourceNameAndActive(cellValue, true);
                                    if (mulberrySource != null) {
                                        farmerLandDetails.setMulberrySourceId(farmerLandDetails.getMulberrySourceId());
                                    }
                                }
                                break;

                            case 25:
                                //Rearing House
                                System.out.print("Rearing House:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerLandDetails.setRearingHouseDetails(cellValue);
                                }
                                break;


                            case 26:
                                //Mulberry Area Acre
                                System.out.print("Mulberry Area:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerLandDetails.setMulberryArea(cellValue);
                                }
                                break;

                            case 27:
                                //Rearing House RoofType
                                System.out.print("Rearing House RoofType:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    RoofType roofType = roofTypeRepository.findByRoofTypeNameAndActive(cellValue, true);
                                    if (roofType != null) {
                                        farmerLandDetails.setRoofTypeId(farmerLandDetails.getRoofTypeId());
                                    }
                                }
                                break;

                            case 28:
                                //Mulberry Variety
                                System.out.print("Mulberry Variety:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    MulberryVariety mulberryVariety = mulberryVarietyRepository.findByMulberryVarietyNameAndActive(cellValue, true);
                                    if (mulberryVariety != null) {
                                        farmerLandDetails.setMulberryVarietyId(farmerLandDetails.getMulberryVarietyId());
                                    }
                                }
                                break;

                            case 29:
                                //Silk Worm Variety
                                System.out.print("SilkWorm Variety:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    SilkWormVariety silkWormVariety = silkWormVarietyRepository.findBySilkWormVarietyNameAndActive(cellValue, true);
                                    if (silkWormVariety != null) {
                                        farmerLandDetails.setSilkWormVarietyId(farmerLandDetails.getSilkWormVarietyId());
                                    }
                                }
                                break;


                            case 30:
                                //Bank Details BankName
                                System.out.print("Bank Name:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerBankAccount.setFarmerBankName(cellValue);

                                }
                                break;

                            case 31:
                                //Bank Account Number
                                System.out.print("Bank Account Number:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerBankAccount.setFarmerBankAccountNumber(cellValue);

                                }
                                break;

                            case 32:
                                //Bank Branch Name
                                System.out.print("Bank Branch Name:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerBankAccount.setFarmerBankBranchName(cellValue);

                                }
                                break;


                            case 33:
                                //Bank IFSC code
                                System.out.print("Ifsc code:" + cellValue + "\t");
                                if (!cellValue.equals("") && cellValue != null) {
                                    farmerBankAccount.setFarmerBankIfscCode(cellValue);

                                }
                                break;


                        }
                    }

                    // Check if this is the last cell in the row
                    if (row.getLastCellNum() > 0 && row.getLastCellNum() == row.getPhysicalNumberOfCells()) {
                        System.out.println("\nEnd of Row " + rowNumber);
                    }

                    FarmerResponse savedFarmerResponse = farmerService.insertFarmerDetails(farmer);

                    if (savedFarmerResponse.getError() == false) {
                        farmerAddress.setFarmerId(savedFarmerResponse.getFarmerId());
                        farmerLandDetails.setFarmerId(savedFarmerResponse.getFarmerId());
                        farmerBankAccount.setFarmerId(savedFarmerResponse.getFarmerId());

                        FarmerAddressResponse savedFarmerAddressResponse = farmerAddressService.insertFarmerAddressDetails(farmerAddress);
                        FarmerLandDetailsResponse savedFarmerLandDetailsResponse = farmerLandDetailsService.insertFarmerLandDetailsDetails(farmerLandDetails);
                        FarmerBankAccountResponse savedFarmerBankAccountResponse = farmerBankAccountService.insertFarmerBankAccountDetails(farmerBankAccount);

                    }

                }
                System.out.println();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return "OK";
    }
}
