using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace CSharpApp1
{
    class Program
    {
        static void Main(string[] args)
        {
            var endpoint = "***.service.signalr.net";
            var key = "";
            Console.WriteLine("AzureSignalRServerToken:");
            Console.WriteLine(GenerateJwtBearer(null, $"https://{endpoint}:5002/api/v1-preview/hub/signin", null, DateTime.UtcNow.AddMonths(30), key));
            Console.WriteLine("AzureSignalRClientToken:");
            Console.WriteLine(GenerateJwtBearer(null, $"https://{endpoint}:5001/client/?hub=signin", null, DateTime.UtcNow.AddMonths(30), key));
        }

        static string GenerateJwtBearer(string issuer, string audience, ClaimsIdentity subject, DateTime? expires, string signingKey)
        {
            JwtSecurityTokenHandler jwtTokenHandler = new JwtSecurityTokenHandler();
            SigningCredentials credentials = null;
            if (!string.IsNullOrEmpty(signingKey))
            {
                var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(signingKey));
                credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);
            }

            var token = jwtTokenHandler.CreateJwtSecurityToken(
                issuer: issuer,
                audience: audience,
                subject: subject,
                expires: expires,
                signingCredentials: credentials);
            return jwtTokenHandler.WriteToken(token);
        }
    }
}
