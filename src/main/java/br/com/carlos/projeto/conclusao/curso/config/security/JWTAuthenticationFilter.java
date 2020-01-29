package br.com.carlos.projeto.conclusao.curso.config.security;

import static br.com.carlos.projeto.conclusao.curso.config.security.SecurityConstants.HEADER_STRING;
import static br.com.carlos.projeto.conclusao.curso.config.security.SecurityConstants.SECRET;
import static br.com.carlos.projeto.conclusao.curso.config.security.SecurityConstants.TOKEN_PREFIX;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.carlos.projeto.conclusao.curso.model.UserModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager)
	{
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException
	{
		try
		{
			UserModel userModel = new ObjectMapper().readValue(request.getInputStream(), UserModel.class);
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userModel.getUsername(), userModel.getPassword()));
		} catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException
	{

		String username = ((User) authResult.getPrincipal()).getUsername();
		String token = Jwts.builder().setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

	}

}
