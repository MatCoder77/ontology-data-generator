package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.profiles.Profiles;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OWLOntologyValidator {

    private static final Set<OWLProfile> SUPPORTED_PROFILES = ImmutableSet.of(Profiles.OWL2_FULL);

    void validate(OWLOntology owlOntology) {
        validateOntologyProfile(owlOntology);
        validateIfOntologyContainsOnlyBuildInDatatypes(owlOntology);
        validateIfOntologyIsConsistent(owlOntology);
    }

    private void validateOntologyProfile(OWLOntology ontology) {
        if (!hasSupportedProfile(ontology)) {
            throw new IllegalStateAppException(MessageFormat.format("Supplied ontology has unsupported profile. Supported profiles are {0}.", SUPPORTED_PROFILES));
        }
    }

    private boolean hasSupportedProfile(OWLOntology ontology) {
        List<OWLProfile> profilesConformingWithOntology = getProfilesConformingWithOntology(ontology);
        return isAnyProfileSupported(profilesConformingWithOntology);
    }

    private List<OWLProfile> getProfilesConformingWithOntology(OWLOntology ontology) {
        List<OWLProfileReport> profileReports = getProfileReports(ontology);
        logInformationAboutProfileReports(profileReports);
        return profileReports.stream()
                .filter(OWLProfileReport::isInProfile)
                .map(OWLProfileReport::getProfile)
                .collect(Collectors.toList());
    }

    private List<OWLProfileReport> getProfileReports(OWLOntology ontology) {
        return Arrays.stream(Profiles.values())
                .map(profile -> profile.checkOntology(ontology))
                .collect(Collectors.toList());
    }

    private void logInformationAboutProfileReports(List<OWLProfileReport> profileReports) {
        profileReports.forEach(this::logInformationAboutProfileReport);
    }

    private void logInformationAboutProfileReport(OWLProfileReport profileReport) {
        if (profileReport.isInProfile()) {
            log.info("Ontology is in profile {}", profileReport.getProfile().getName());
        } else {
            log.info("Ontology is not in profile {}", profileReport.getProfile().getName());
            log.debug("Violations: {}", System.lineSeparator() + buildViolationsMessage(profileReport.getViolations()));
        }
    }

    private String buildViolationsMessage(List<OWLProfileViolation> violations) {
        return violations.stream()
                .map(OWLProfileViolation::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private boolean isAnyProfileSupported(List<OWLProfile> profilesConformingWithOntology) {
        return profilesConformingWithOntology.stream()
                .map(OWLProfile::getIRI)
                .map(Profiles::valueForIRI)
                .anyMatch(SUPPORTED_PROFILES::contains);
    }

    private void validateIfOntologyContainsOnlyBuildInDatatypes(OWLOntology ontology) {
        ontology.getDatatypesInSignature().forEach(this::validateIfIsBuildInDatatype);
    }

    private void validateIfIsBuildInDatatype(OWLDatatype datatype) {
        if (!datatype.isBuiltIn()) {
            throw new IllegalStateAppException(MessageFormat.format("Ontology contains custom data type {0}. Generation of values is supported only for build in data types!", datatype));
        }
    }

    private void validateIfOntologyIsConsistent(OWLOntology ontology) {
        if (!isOntologyConsistent(ontology)) {
            throw new IllegalStateAppException("Ontology is inconsistent");
        }
    }

    public boolean isOntologyConsistent(OWLOntology ontology) {
        OWLReasoner resasoner = getResasoner(ontology);
        return resasoner.isConsistent();
    }

    private OWLReasoner getResasoner(OWLOntology ontology) {
        return new ReasonerFactory().createReasoner(ontology);
    }

}
