import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@GetMapping("/polls")
@Timed(name = "getPolls", description = "A measure of how long it takes get polls.", unit = MetricUnits.MILLISECONDS)
public ResponseEntity<List<Poll>> retrieveAllpolls() {
    // On récupère la liste de tous les poll qu'on trie ensuite par titre
    List<Poll> polls = pollRepository.findAll(Sort.by("title", Sort.Direction.Ascending)).list();
    return new ResponseEntity<>(polls, HttpStatus.OK);
}

@PostMapping("/polls")
@Transactional
@Timed(name = "postPolls", description = "A measure of how long it takes post polls.", unit = MetricUnits.MILLISECONDS)
@Counted(description = "How polls posted", absolute = true, name = "countPostPolls")
public ResponseEntity<Poll> createPoll(@Valid @RequestBody Poll poll) {
    // On enregistre le poll dans la bdd
    
    String padId = generateSlug(15);
    if (this.usePad) {
        if (client == null) {
            client = new EPLiteClient(padUrl, apikey);
        }
        client.createPad(padId);
        initPad(poll.getTitle(), poll.getLocation(), poll.getDescription(), client, padId);
        poll.setPadURL(externalPadUrl + "p/" + padId);
    } 
    pollRepository.persist(poll);
    return new ResponseEntity<>(poll, HttpStatus.CREATED);
}