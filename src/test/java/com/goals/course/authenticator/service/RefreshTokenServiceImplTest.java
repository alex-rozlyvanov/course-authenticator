package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.RefreshTokenRepository;
import com.goals.course.authenticator.exception.TokenRefreshException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private Clock mockClock;

    @InjectMocks
    private RefreshTokenService service;

    @Test
    void verifyExpiration_tokenIsExpired_callDelete() {
        // GIVEN
        final var token = new RefreshToken()
                .setExpiryDate(Instant.now());
        when(mockRefreshTokenRepository.delete(any())).thenReturn(Mono.just(1));

        // WHEN
        final var mono = service.verifyExpiration(token);

        // THEN
        StepVerifier.create(mono).verifyError();
        verify(mockRefreshTokenRepository).delete(token);
    }

    @Test
    void verifyExpiration_tokenIsExpired_checkResult() {
        // GIVEN
        final var token = new RefreshToken()
                .setExpiryDate(Instant.now());
        when(mockRefreshTokenRepository.delete(any())).thenReturn(Mono.just(1));

        // WHEN
        final var mono = service.verifyExpiration(token);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(exception -> {
                    assertThat(exception).isInstanceOf(TokenRefreshException.class);
                    assertThat(exception).hasMessage("Refresh token was expired. Please make a new signin request");
                })
                .verify();
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    @Test
    void refreshToken_callFindByToken() {
        // GIVEN
        final var spyService = spy(service);
        lenient().doReturn(Mono.just(true)).when(spyService).verifyExpiration(any());
        lenient().doReturn(Mono.just("refreshToken")).when(spyService).createRefreshToken(any());

        final var refreshToken = buildRefreshToken();
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        // WHEN
        final var mono = spyService.refreshToken("refreshToken_test_123");

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRefreshTokenRepository).findByToken("refreshToken_test_123");
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    @Test
    void refreshToken_callVerifyExpiration() {
        // GIVEN
        final var spyService = spy(service);
        lenient().doReturn(Mono.just(true)).when(spyService).verifyExpiration(any());
        lenient().doReturn(Mono.just("refreshToken")).when(spyService).createRefreshToken(any());

        final var refreshToken = buildRefreshToken();
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        // WHEN
        final var mono = spyService.refreshToken(null);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(spyService).verifyExpiration(refreshToken);
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    @Test
    void refreshToken_callGenerateAccessToken() {
        // GIVEN
        final var spyService = spy(service);
        lenient().doReturn(Mono.just(true)).when(spyService).verifyExpiration(any());
        lenient().doReturn(Mono.just("refreshToken")).when(spyService).createRefreshToken(any());

        final var refreshToken = buildRefreshToken();
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        // WHEN
        spyService.refreshToken(null).subscribe();

        // THEN
        verify(mockJwtTokenService).generateAccessToken(refreshToken.getUser());
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    @Test
    void refreshToken_callCreateRefreshToken() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        lenient().doReturn(Mono.just(true)).when(spyService).verifyExpiration(any());
        lenient().doReturn(Mono.just("refreshToken")).when(spyService).createRefreshToken(any());

        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        // WHEN
        spyService.refreshToken(null).subscribe();

        // THEN
        verify(spyService).createRefreshToken(refreshToken.getUser());
    }

    @SuppressWarnings("ReactiveStreamsUnusedPublisher")
    @Test
    void refreshToken_checkResult() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        lenient().doReturn(Mono.just(true)).when(spyService).verifyExpiration(any());
        lenient().doReturn(Mono.just("new_refresh_token")).when(spyService).createRefreshToken(any());

        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("new_access_token");

        // WHEN
        final var mono = spyService.refreshToken(null);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result.getAccessToken()).isEqualTo("new_access_token");
                    assertThat(result.getRefreshToken()).isEqualTo("new_refresh_token");
                })
                .verifyComplete();
    }

    @Test
    void refreshToken_refreshTokenNotFound_throwException() {
        // GIVEN
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.refreshToken(null);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(exception -> {
                    assertThat(exception).isInstanceOf(TokenRefreshException.class);
                    assertThat(exception).hasMessage("Refresh token is not valid!");
                })
                .verify();
    }

    private RefreshToken buildRefreshToken() {
        final User user = buildUser();
        return new RefreshToken().setUser(user).setExpiryDate(Instant.now().plusSeconds(100));
    }

    @Test
    void createRefreshToken_callGenerateRefreshToken() {
        // GIVEN
        final User user = buildUser();

        when(mockClock.instant()).thenReturn(Instant.now());
        when(mockRefreshTokenRepository.deleteByUserId(any())).thenReturn(Mono.just(1));
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken().setToken("")));

        // WHEN
        final var mono = service.createRefreshToken(user);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockJwtTokenService).generateRefreshToken(user);
    }

    @Test
    void createRefreshToken_callDeleteByUserId() {
        // GIVEN
        when(mockClock.instant()).thenReturn(Instant.now());
        when(mockRefreshTokenRepository.deleteByUserId(any())).thenReturn(Mono.just(1));

        final var refreshToken = buildRefreshToken();
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken().setToken("")));

        // WHEN
        final var mono = service.createRefreshToken(buildUser());

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRefreshTokenRepository).deleteByUserId(refreshToken.getUser().getId());
    }

    @Test
    void createRefreshToken_callSave() {
        // GIVEN
        final User user = buildUser();

        when(mockRefreshTokenRepository.deleteByUserId(any())).thenReturn(Mono.just(1));
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken().setToken("")));
        when(mockJwtTokenService.getRefreshTokenExpirationMs()).thenReturn(65198197161L);
        final var now = Instant.now();
        when(mockClock.instant()).thenReturn(now);

        // WHEN
        final var mono = service.createRefreshToken(user);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();

        final var captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(mockRefreshTokenRepository).save(captor.capture());

        final var refreshToken = captor.getValue();
        final var expirationTime = now.plusMillis(65198197161L);
        assertThat(refreshToken.getUser()).isSameAs(user);
        assertThat(refreshToken.getExpiryDate()).isEqualTo(expirationTime);
    }

    @Test
    void createRefreshToken_callDeleteByUserIdInOrder() {
        // GIVEN
        when(mockClock.instant()).thenReturn(Instant.now());
        when(mockRefreshTokenRepository.deleteByUserId(any())).thenReturn(Mono.just(1));
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(new RefreshToken().setToken("")));

        // WHEN
        final var mono = service.createRefreshToken(buildUser());

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        final var inOrder = inOrder(mockRefreshTokenRepository);
        inOrder.verify(mockRefreshTokenRepository).deleteByUserId(any());
        inOrder.verify(mockRefreshTokenRepository).save(any());
    }

    @Test
    void createRefreshToken_checkResult() {
        // GIVEN
        when(mockClock.instant()).thenReturn(Instant.now());
        when(mockRefreshTokenRepository.deleteByUserId(any())).thenReturn(Mono.just(1));
        final var refreshToken = new RefreshToken().setToken("new_refresh_token");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(refreshToken));

        // WHEN
        final var mono = service.createRefreshToken(buildUser());

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result).isSameAs("new_refresh_token"))
                .verifyComplete();
    }

    private User buildUser() {
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return new User().setId(userId);
    }

    @Test
    void deleteRefreshTokenByUserId_callDeleteByUserId() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(mockRefreshTokenRepository.deleteByUserId(any())).thenReturn(Mono.just(1));

        // WHEN
        final var mono = service.deleteRefreshTokenByUserId(userId);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRefreshTokenRepository).deleteByUserId(userId);
    }
}
