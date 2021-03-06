package com.apress.controller;

import java.net.URI;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.apress.domain.Poll;
import com.apress.exception.ResourceNotFoundException;
import com.apress.repository.PollRepository;

@RestController
public class PollController {

        @Inject
        private PollRepository pollRepository;
        @RequestMapping(value="/polls", method=RequestMethod.GET)
        public ResponseEntity<Iterable<Poll>> getAllPolls() {
                Iterable<Poll> allPolls = pollRepository.findAll();
                return new ResponseEntity<>(pollRepository.findAll(), HttpStatus.OK);
        }
        
        @RequestMapping(value="/polls", method=RequestMethod.POST)
        public ResponseEntity<?> createPoll(@Valid @RequestBody Poll poll) {

                poll = pollRepository.save(poll);
                
                // Set the location header for the newly created resource
                HttpHeaders responseHeaders = new HttpHeaders();
                URI newPollUri = ServletUriComponentsBuilder
                                                      .fromCurrentRequest()
                                                      .path("/{id}")
                                                      .buildAndExpand(poll.getId())
                                                      .toUri();
                responseHeaders.setLocation(newPollUri);
                
                return new ResponseEntity<>(null, HttpStatus.CREATED);
        }
        
	protected void verifyPoll(Long pollId) throws ResourceNotFoundException {
            Poll poll = pollRepository.findOne(pollId);
            if(poll == null) {
                    throw new ResourceNotFoundException("Poll with id " + pollId + " not found");
            }
    }

    @RequestMapping(value="/polls/{pollId}", method=RequestMethod.GET)
    public ResponseEntity<?> getPoll(@PathVariable Long pollId) {
            verifyPoll(pollId);
            Poll p = pollRepository.findOne(pollId);
            return new ResponseEntity<> (p, HttpStatus.OK);
    }

    @RequestMapping(value="/polls/{pollId}", method=RequestMethod.PUT)
    public ResponseEntity<?> updatePoll(@RequestBody Poll poll, @PathVariable Long pollId) {
            verifyPoll(pollId);
            pollRepository.save(poll);
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value="/polls/{pollId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deletePoll(@PathVariable Long pollId) {
            verifyPoll(pollId);
            pollRepository.delete(pollId);
            return new ResponseEntity<>(HttpStatus.OK);
    }
}