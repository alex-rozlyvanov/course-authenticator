package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.RefreshTokenRepository;
import com.goals.course.authenticator.exception.TokenRefreshException;
import com.goals.course.authenticator.service.InstantWrapper;
import com.goals.course.authenticator.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private InstantWrapper mockInstantWrapper;

    @InjectMocks
    private RefreshTokenServiceImpl service;

    @Test
    void verifyExpiration_tokenIsNotExpired_checkResult() {
        // GIVEN
        final var token = new RefreshToken()
                .setExpiryDate(Instant.now().plusMillis(10));

        // WHEN
        final var result = service.verifyExpiration(token);

        // THEN
        assertThat(result).isSameAs(token);
    }

    @Test
    void verifyExpiration_tokenIsExpired_checkResult() {
        // GIVEN
        final var token = new RefreshToken()
                .setExpiryDate(Instant.now());

        // WHEN
        final var expectedException = assertThrows(TokenRefreshException.class, () -> service.verifyExpiration(token));

        // THEN
        assertThat(expectedException.getMessage())
                .isEqualTo("Refresh token was expired. Please make a new signin request");
    }

    @Test
    void refreshToken_callFindByToken() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        doReturn(refreshToken).when(spyService).verifyExpiration(any());
        doReturn(null).when(spyService).createRefreshToken(any());
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));

        // WHEN
        spyService.refreshToken("refreshToken_test_123");

        // THEN
        verify(mockRefreshTokenRepository).findByToken("refreshToken_test_123");
    }

    @Test
    void refreshToken_callVerifyExpiration() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        doReturn(refreshToken).when(spyService).verifyExpiration(any());
        doReturn(null).when(spyService).createRefreshToken(any());
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));

        // WHEN
        spyService.refreshToken(null);

        // THEN
        verify(spyService).verifyExpiration(refreshToken);
    }

    @Test
    void refreshToken_callGenerateAccessToken() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        doReturn(refreshToken).when(spyService).verifyExpiration(any());
        doReturn(null).when(spyService).createRefreshToken(any());
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));

        // WHEN
        spyService.refreshToken(null);

        // THEN
        verify(mockJwtTokenService).generateAccessToken(refreshToken.getUser());
    }

    @Test
    void refreshToken_callCreateRefreshToken() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        doReturn(refreshToken).when(spyService).verifyExpiration(any());
        doReturn(null).when(spyService).createRefreshToken(any());
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));

        // WHEN
        spyService.refreshToken(null);

        // THEN
        verify(spyService).createRefreshToken(refreshToken.getUser());
    }

    @Test
    void refreshToken_checkResult() {
        // GIVEN
        final var spyService = spy(service);
        final var refreshToken = buildRefreshToken();
        doReturn(refreshToken).when(spyService).verifyExpiration(any());
        doReturn("new_refresh_token").when(spyService).createRefreshToken(any());
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("new_access_token");

        // WHEN
        final var result = spyService.refreshToken(null);

        // THEN
        assertThat(result.getAccessToken()).isEqualTo("new_access_token");
        assertThat(result.getRefreshToken()).isEqualTo("new_refresh_token");
    }

    @Test
    void refreshToken_refreshTokenNotFound_throwException() {
        // GIVEN
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // WHEN
        final var expectedException = assertThrows(TokenRefreshException.class, () -> service.refreshToken(null));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Refresh token is not valid!");
    }

    private RefreshToken buildRefreshToken() {
        final User user = buildUser();
        return new RefreshToken().setUser(user);
    }

    @Test
    void createRefreshToken_callGenerateRefreshToken() {
        // GIVEN
        final User user = buildUser();

        when(mockInstantWrapper.now()).thenReturn(Instant.now());
        when(mockRefreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        // WHEN
        service.createRefreshToken(user);

        // THEN
        verify(mockJwtTokenService).generateRefreshToken(user);
    }

    @Test
    void createRefreshToken_callDeleteByUserId() {
        // GIVEN
        when(mockInstantWrapper.now()).thenReturn(Instant.now());

        final var refreshToken = buildRefreshToken();
        when(mockRefreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        // WHEN
        service.createRefreshToken(buildUser());

        // THEN
        verify(mockRefreshTokenRepository).deleteByUserId(refreshToken.getUser().getId());
    }

    @Test
    void createRefreshToken_callSave() {
        // GIVEN
        final User user = buildUser();

        when(mockRefreshTokenRepository.save(any())).thenReturn(new RefreshToken());
        when(mockJwtTokenService.getRefreshTokenExpirationMs()).thenReturn(65198197161L);
        final var now = Instant.now();
        when(mockInstantWrapper.now()).thenReturn(now);

        // WHEN
        service.createRefreshToken(user);

        // THEN
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
        when(mockInstantWrapper.now()).thenReturn(Instant.now());
        when(mockRefreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        // WHEN
        service.createRefreshToken(buildUser());

        // THEN
        final var inOrder = inOrder(mockRefreshTokenRepository);
        inOrder.verify(mockRefreshTokenRepository).deleteByUserId(any());
        inOrder.verify(mockRefreshTokenRepository).save(any());
    }

    @Test
    void createRefreshToken_checkResult() {
        // GIVEN
        when(mockInstantWrapper.now()).thenReturn(Instant.now());
        final var refreshToken = new RefreshToken().setToken("new_refresh_token");
        when(mockRefreshTokenRepository.save(any())).thenReturn(refreshToken);

        // WHEN
        final var result = service.createRefreshToken(buildUser());

        // THEN
        assertThat(result).isSameAs("new_refresh_token");
    }

    private User buildUser() {
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return new User().setId(userId);
    }

    @Test
    void deleteRefreshTokenByUserId_callDeleteByUserId() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // WHEN
        service.deleteRefreshTokenByUserId(userId);

        // THEN
        verify(mockRefreshTokenRepository).deleteByUserId(userId);
    }
}
