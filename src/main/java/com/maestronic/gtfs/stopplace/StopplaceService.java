package com.maestronic.gtfs.stopplace;

import com.maestronic.gtfs.util.GlobalVariable;
import com.maestronic.gtfs.util.Logger;
import nl.connekt.bison.chb.Quay;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class StopplaceService {

    @PersistenceContext
    private EntityManager entityManager;
    private Session session;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private StopplaceRepository stopplaceRepository;

    private Session getSession() {
        if (session == null) session = entityManager.unwrap(Session.class);
        return session;
    }

    private void checkBatchSize(int dataCount) {
        if (dataCount % batchSize == 0) {
            session.flush();
            session.clear();
        }
    }

    public Integer parseSaveData(List<nl.connekt.bison.chb.Stopplace> xmlData) {

        getSession();
        int dataCount = 0;
        try {
            // Delete all data in table
            stopplaceRepository.deleteAllData();
            for (nl.connekt.bison.chb.Stopplace record : xmlData) {
                Stopplace stopplace = new Stopplace(
                        record.getPlacecode(),
                        record.getStopplacecode(),
                        record.getStopplacetype(),
                        record.getStopplacename() == null ? null : record.getStopplacename().getPublicname(),
                        record.getStopplacename() == null ? null : record.getStopplacename().getTown(),
                        record.getStopplacename() == null ? null : record.getStopplacename().getStreet(),
                        record.getStopplacestatusdata() == null ? null : record.getStopplacestatusdata().getStopplacestatus(),
                        record.getUiccode(),
                        record.getStopplacelocation() == null ? null : record.getStopplacelocation().getLevel(),
                        record.getStopplacelocation() == null ? null : record.getStopplacelocation().getRdX(),
                        record.getStopplacelocation() == null ? null : record.getStopplacelocation().getRdY(),
                        record.getStopplaceowner() == null ? null : record.getStopplaceowner().getStopplaceownercode(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isTimetableinformation(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isPassengerinformationdisplay(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().getPassengerinformationdisplaytype(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isBicycleparking(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().getNumberofbicycleplaces(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isToiletfacility(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isPtbikerental(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isBins(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isOvccico(),
                        record.getStopplacefacilities() == null ? null : record.getStopplacefacilities().isOvccharging()
                );

                // Add quay
                if (record.getQuays() != null) {
                    for (Quay quay : record.getQuays().getQuay()) {
                        com.maestronic.gtfs.stopplace.Quay quays = new com.maestronic.gtfs.stopplace.Quay();

                        quays.setQuaycode(quay.getQuaycode());
                        quays.setQuaytype(quay.getQuaytypedata() == null ? null : quay.getQuaytypedata().getQuaytype());
                        quays.setTransportmode(quay.getQuaytransportmodes() == null ? null : quay.getQuaytransportmodes().getTransportmodedata().get(0).getTransportmode());
                        quays.setQuaystatus(quay.getQuaystatusdata() == null ? null : quay.getQuaystatusdata().getQuaystatus());
                        quays.setRd_x(quay.getQuaylocationdata() == null ? null : quay.getQuaylocationdata().getRdX());
                        quays.setRd_y(quay.getQuaylocationdata() == null ? null : quay.getQuaylocationdata().getRdY());
                        quays.setTown(quay.getQuaylocationdata() == null ? null : quay.getQuaylocationdata().getTown());
                        quays.setLevel(quay.getQuaylocationdata() == null ? null : quay.getQuaylocationdata().getLevel());
                        quays.setStreet(quay.getQuaylocationdata() == null ? null : quay.getQuaylocationdata().getStreet());
                        quays.setLocation(quay.getQuaylocationdata() == null ? null : quay.getQuaylocationdata().getLocation());
                        quays.setCompassdirection(quay.getQuaybearing() == null ? null : quay.getQuaybearing().getCompassdirection());
                        quays.setVisuallyaccessible(quay.getQuayvisuallyaccessible() == null ? null : quay.getQuayvisuallyaccessible().getVisuallyaccessible());
                        quays.setDisabledaccessible(quay.getQuaydisabledaccessible() == null ? null : quay.getQuaydisabledaccessible().get(0).getDisabledaccessible());
                        quays.setOnlygetout(quay.isOnlygetout());
                        quays.setMunicipalitycode(quay.getQuaymunicipality() == null ? null : quay.getQuaymunicipality().getMunicipalitycode());
                        quays.setQuayownercode(quay.getQuayowner() == null ? null : quay.getQuayowner().getQuayownercode());
                        quays.setConcessionprovidercode(quay.getQuayconcessionprovider() == null ? null : quay.getQuayconcessionprovider().getConcessionprovidercode());
                        quays.setQuayname(quay.getQuaynamedata() == null ? null : quay.getQuaynamedata().getQuayname());
                        quays.setStopsidecode(quay.getQuaynamedata() == null ? null : quay.getQuaynamedata().getStopsidecode());
                        quays.setQuayshapetype(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getQuayshapetype());
                        quays.setBaylength(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getBaylength());
                        quays.setMarkedkerb(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isMarkedkerb());
                        quays.setLift(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isLift());
                        quays.setGuidelines(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isGuidelines());
                        quays.setGroundsurfaceindicator(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isGroundsurfaceindicator());
                        quays.setStopplaceaccessroute(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isStopplaceaccessroute());
                        quays.setEmbaymentwidth(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getEmbaymentwidth());
                        quays.setBayentranceangles(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getBayentranceangles());
                        quays.setBayexitangles(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getBayexitangles());
                        quays.setKerbheight(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getKerbheight());
                        quays.setBoardingpositionwidth(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getBoardingpositionwidth());
                        quays.setAlightingpositionwidth(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getAlightingpositionwidth());
                        quays.setLiftedpartlength(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getLiftedpartlength());
                        quays.setNarrowestpassagewidth(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getNarrowestpassagewidth());
                        quays.setFulllengthguideline(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isFulllengthguideline());
                        quays.setGuidelinestopplaceconnection(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isGuidelinestopplaceconnection());
                        quays.setTactilegroundsurfaceindicator(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isTactilegroundsurfaceindicator());
                        quays.setRamp(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().isRamp());
                        quays.setRamplength(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getRamplength());
                        quays.setHeightwithenvironment(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getHeightwithenvironment());
                        quays.setRampwidth(quay.getQuayaccessibilityadaptions() == null ? null : quay.getQuayaccessibilityadaptions().getRampwidth());
                        quays.setStopsign(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isStopsign());
                        quays.setAudiobutton(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isAudiobutton());
                        quays.setStopsigntype(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().getStopsigntype());
                        quays.setShelter(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isShelter());
                        quays.setShelterpublicity(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isShelterpublicity());
                        quays.setIlluminatedstop(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isIlluminatedstop());
                        quays.setSeatavailable(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isSeatavailable());
                        quays.setLeantosupport(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isLeantosupport());
                        quays.setTimetableinformation(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isTimetableinformation());
                        quays.setInfounit(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isInfounit());
                        quays.setRoutenetworkmap(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isRoutenetworkmap());
                        quays.setPassengerinformationdisplay(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isPassengerinformationdisplay());
                        quays.setPassengerinformationdisplaytype(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().getPassengerinformationdisplaytype());
                        quays.setBicycleparking(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isBicycleparking());
                        quays.setNumberofbicycleplaces(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().getNumberofbicycleplaces());
                        quays.setBins(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isBins());
                        quays.setOvccico(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isOvccico());
                        quays.setOvccharging(quay.getQuayfacilities() == null ? null : quay.getQuayfacilities().isOvccharging());
                        quays.setRemarks(quay.getQuayremarks() == null ? null : quay.getQuayremarks().getRemarks());
                        quays.setRoadcode(quay.getQuayextraattributes() == null ? null : quay.getQuayextraattributes().getRoadcode());
                        quays.setHectometersign(quay.getQuayextraattributes() == null ? null : quay.getQuayextraattributes().getHectometersign());
                        quays.setGreenstop(quay.getQuayextraattributes() == null ? null : quay.getQuayextraattributes().isGreenstop());
                        quays.setLiftedbicyclepath(quay.getQuayextraattributes() == null ? null : quay.getQuayextraattributes().isLiftedbicyclepath());
                        quays.setStopplace(stopplace);

                        stopplace.getQuays().add(quays);
                    }
                }

                session.save(stopplace);
                // compare batch saved count
                dataCount++;
                checkBatchSize(dataCount);
            }
            session.flush();
            session.clear();
            Logger.info("Parse and save " + GlobalVariable.STOPPLACES + " file is complete.");
            return dataCount;
        } catch (Exception e) {
            String logMessage = "Fail to parse and save " + GlobalVariable.STOPPLACES + ". " + e.getMessage();
            throw new RuntimeException(logMessage);
        }

    }
}
