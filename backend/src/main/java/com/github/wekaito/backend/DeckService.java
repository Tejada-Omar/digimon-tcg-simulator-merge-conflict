package com.github.wekaito.backend;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepo deckRepo;

    private final IdService idService;

    private final UserIdService userIdService;

    private Card[] fetchedCards;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://digimoncard.io/api-public")
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(1024 * 1024 * 10))
                    .build())
            .build();

    @PostConstruct
    public void init() {
        fetchCards();
    }

    @Scheduled(fixedRate = 600000) // 10 minutes
    void fetchCards() {
        ResponseEntity<Card[]> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.php")
                        .queryParam("sort", "name")
                        .queryParam("series", "Digimon Card Game")
                        .build())
                .retrieve()
                .toEntity(Card[].class)
                .block();
        fetchedCards = Objects.requireNonNull(response).getBody();
    }

    public Card[] getCards() {
        return fetchedCards;
    }

    public void addDeck(DeckWithoutId deckWithoutId) {
        Deck deckToSave = new Deck(
                idService.createId(),
                deckWithoutId.name(),
                deckWithoutId.cards(),
                userIdService.getCurrentUserId()
        );
        this.deckRepo.save(deckToSave);
    }

    public List<Deck> getDecksByAuthorId(String authorId) {
        return deckRepo.findByAuthorId(authorId);
    }

    public void updateDeck(String id, DeckWithoutId deckWithoutId) {
        if (!deckRepo.existsById(id)) throw new IllegalArgumentException();
        Deck deckToSave = new Deck(
                id,
                deckWithoutId.name(),
                deckWithoutId.cards(),
                userIdService.getCurrentUserId()
        );
        this.deckRepo.save(deckToSave);
    }

    public Deck getDeckById(String id) {
        Optional<Deck> optionalDeck = this.deckRepo.findById(id);
        return optionalDeck.orElse(null);
    }

    public void deleteDeck(String id) {
        if (!deckRepo.existsById(id)) throw new IllegalArgumentException();
        this.deckRepo.deleteById(id);
    }
}
