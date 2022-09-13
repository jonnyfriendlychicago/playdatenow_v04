package com.jonfriend.playdatenow_v04.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.jonfriend.playdatenow_v04.models.PlaydateMdl;
import com.jonfriend.playdatenow_v04.repositories.PlaydateRpo;

@Service
public class PlaydateSrv {
	
	// adding the playdate repository as a dependency
	private final PlaydateRpo playdateRpo;
	
	public PlaydateSrv(PlaydateRpo playdateRpo) {this.playdateRpo = playdateRpo;}
	
	// creates one playdate 
	public PlaydateMdl create(PlaydateMdl x) {
		return playdateRpo.save(x);
	}

	// updates one playdate 
	public PlaydateMdl update(PlaydateMdl x) {
		return playdateRpo.save(x);
	}
	
	// delete playdate by id 
	public void delete(PlaydateMdl x) {
		playdateRpo.delete(x);
	}
	
	// returns one playdate by id 
	public PlaydateMdl findById(Long id) {
		Optional<PlaydateMdl> optionalPlaydate = playdateRpo.findById(id);
		if(optionalPlaydate.isPresent()) {
			return optionalPlaydate.get();
		}else {
			return null;
		}
	}
	
	// returns all playdate 
	public List<PlaydateMdl> returnAll(){
		return playdateRpo.findAll();
	}
	
	public List<PlaydateMdl> userHostedPlaydateListCurrentPlus(Long x) {
		return playdateRpo.userHostedPlaydateListCurrentPlus(x); 
	}
	
	public List<PlaydateMdl> userHostedPlaydateListPast(Long x) {
		return playdateRpo.userHostedPlaydateListPast(x); 
	}
	
// end srv
}