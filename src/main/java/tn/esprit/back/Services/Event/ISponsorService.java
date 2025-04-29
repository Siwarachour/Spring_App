package tn.esprit.back.Services.Event;


import tn.esprit.back.Entities.Event.Sponsor;

import java.util.List;

public interface ISponsorService {


    Sponsor addSponsor (Sponsor sponsor);
    Sponsor updateSponsor(Long idSponsor, Sponsor sponsorDetails);
    void deleteSponsor (long idSponsor);
    List<Sponsor> retriveAllSponsors ();
    Sponsor getSponsorbyId  (long idSponsor);

}
