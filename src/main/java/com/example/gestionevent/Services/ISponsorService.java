package com.example.gestionevent.Services;

import com.example.gestionevent.entities.Sponsor;

import java.util.List;

public interface ISponsorService {
    Sponsor addSponsor (Sponsor Sponsor);
    Sponsor updateSponsor(Long idSponsor, Sponsor sponsorDetails);
    void deleteSponsor (long idSponsor);
    List<Sponsor> retriveAllSponsors ();
    Sponsor getSponsorbyId  (long idSponsor);

}
