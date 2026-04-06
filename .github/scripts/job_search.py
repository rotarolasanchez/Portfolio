#!/usr/bin/env python3
"""
Job Search Script for Senior Android Developer
Searches for Android/Kotlin job openings and sends an email report.

Sources:
- Arbeitnow API (https://www.arbeitnow.com/api/job-board-api)
- Remotive API (https://remotive.com/api/remote-jobs)
- Jobicy RSS Feed (https://jobicy.com/?feed=job_feed)
"""

import os
import re
import smtplib
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

import requests

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

RECIPIENT_EMAIL = "rotarolasanchez@gmail.com"

KEYWORDS = [
    "android",
    "kotlin",
    "jetpack compose",
    "mobile developer",
    "android engineer",
]

# Salary thresholds
MIN_SALARY_PEN = 7000   # S/ soles (Peru)
MIN_SALARY_USD = 1900   # USD per month (international/remote)

REQUEST_TIMEOUT = 15    # seconds per HTTP request

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------


def _contains_keyword(text: str) -> bool:
    """Return True if *text* contains at least one of the target keywords."""
    lower = text.lower()
    return any(kw in lower for kw in KEYWORDS)


def _extract_usd_amount(text: str) -> float | None:
    """Try to extract a USD monthly salary amount from *text*.

    Looks for patterns like:
      - $2,000 / month  → returned as-is
      - $2000/month
      - 2000 USD
      - USD 2000
      - $50,000/year    → divided by 12 to get monthly equivalent
      - $50k per annum  → divided by 12 to get monthly equivalent
    Returns the monthly amount as a float, or None if not found.
    """
    if not text:
        return None
    text_lower = text.lower()

    # "$X,XXX / month" or "$X,XXX/month"
    match = re.search(r"\$\s*([\d,]+(?:\.\d+)?)\s*(?:/\s*mo(?:nth)?|per\s*mo(?:nth)?)", text_lower)
    if match:
        return float(match.group(1).replace(",", ""))

    # "$X,XXX / year" or "$Xk/year" or "$X per annum"
    match = re.search(
        r"\$\s*([\d,]+(?:\.\d+)?)\s*k?\s*(?:/\s*(?:yr|year|annum)|per\s*(?:yr|year|annum))",
        text_lower,
    )
    if match:
        raw = match.group(1).replace(",", "")
        amount = float(raw)
        # Handle "k" shorthand (e.g. $50k/year → 50000)
        if "k" in text_lower[match.start():match.end()]:
            amount *= 1000
        return round(amount / 12, 2)

    # "XXXX USD" or "USD XXXX"
    match = re.search(r"(?:usd\s*([\d,]+(?:\.\d+)?)|(\d[\d,]*(?:\.\d+)?)\s*usd)", text_lower)
    if match:
        raw = match.group(1) or match.group(2)
        return float(raw.replace(",", ""))

    return None


def _extract_pen_amount(text: str) -> float | None:
    """Try to extract a PEN (Peruvian sol) monthly salary amount from *text*."""
    if not text:
        return None
    text_lower = text.lower()

    # "S/ 7,000" or "s/7000"
    match = re.search(r"s/\s*([\d,]+(?:\.\d+)?)", text_lower)
    if match:
        return float(match.group(1).replace(",", ""))

    # "7000 soles"
    match = re.search(r"([\d,]+(?:\.\d+)?)\s*soles", text_lower)
    if match:
        return float(match.group(1).replace(",", ""))

    return None


def _salary_is_acceptable(salary_text: str, location: str) -> bool:
    """Return True when no salary info is present (we include the job) OR when
    the detected salary meets the configured minimums."""
    if not salary_text:
        return True

    combined = (salary_text + " " + location).lower()
    is_peru = "peru" in combined or "perú" in combined or "lima" in combined

    usd = _extract_usd_amount(salary_text)
    pen = _extract_pen_amount(salary_text)

    if is_peru:
        if pen is not None:
            return pen >= MIN_SALARY_PEN
        if usd is not None:
            return usd >= MIN_SALARY_USD
        return True  # salary text present but unrecognised format — include

    # International / remote
    if usd is not None:
        return usd >= MIN_SALARY_USD
    if pen is not None:
        return pen >= MIN_SALARY_PEN
    return True


def _is_remote_or_peru(location: str) -> bool:
    """Return True if the job is remote or based in Peru."""
    lower = location.lower()
    remote_keywords = ["remote", "remoto", "worldwide", "anywhere", "global", "distributed"]
    peru_keywords = ["peru", "perú", "lima"]
    return any(k in lower for k in remote_keywords + peru_keywords)


# ---------------------------------------------------------------------------
# API sources
# ---------------------------------------------------------------------------


def _fetch_arbeitnow() -> list[dict]:
    """Fetch jobs from the Arbeitnow public API."""
    jobs = []
    try:
        url = "https://www.arbeitnow.com/api/job-board-api"
        response = requests.get(url, timeout=REQUEST_TIMEOUT)
        response.raise_for_status()
        data = response.json()
        for item in data.get("data", []):
            title = item.get("title", "")
            company = item.get("company_name", "")
            location = item.get("location", "Remote")
            salary = item.get("salary", "") or ""
            link = item.get("url", "")
            date = item.get("created_at", "")
            description = item.get("description", "")

            combined_text = f"{title} {description}"
            if not _contains_keyword(combined_text):
                continue
            if not _is_remote_or_peru(location):
                continue
            if not _salary_is_acceptable(salary, location):
                continue

            jobs.append({
                "title": title,
                "company": company,
                "location": location,
                "salary": salary,
                "link": link,
                "date": date,
                "source": "Arbeitnow",
            })
    except Exception as exc:
        print(f"[WARNING] Arbeitnow API failed: {exc}", file=sys.stderr)
    return jobs


def _fetch_remotive() -> list[dict]:
    """Fetch jobs from the Remotive public API."""
    jobs = []
    try:
        url = "https://remotive.com/api/remote-jobs"
        params = {"category": "software-dev", "limit": 100}
        response = requests.get(url, params=params, timeout=REQUEST_TIMEOUT)
        response.raise_for_status()
        data = response.json()
        for item in data.get("jobs", []):
            title = item.get("title", "")
            company = item.get("company_name", "")
            location = item.get("candidate_required_location", "Remote")
            salary = item.get("salary", "") or ""
            link = item.get("url", "")
            date = item.get("publication_date", "")
            description = item.get("description", "")

            combined_text = f"{title} {description}"
            if not _contains_keyword(combined_text):
                continue
            if not _salary_is_acceptable(salary, location):
                continue

            jobs.append({
                "title": title,
                "company": company,
                "location": location,
                "salary": salary,
                "link": link,
                "date": date,
                "source": "Remotive",
            })
    except Exception as exc:
        print(f"[WARNING] Remotive API failed: {exc}", file=sys.stderr)
    return jobs


def _fetch_jobicy() -> list[dict]:
    """Fetch jobs from the Jobicy RSS feed."""
    jobs = []
    try:
        url = "https://jobicy.com/?feed=job_feed"
        response = requests.get(url, timeout=REQUEST_TIMEOUT)
        response.raise_for_status()
        root = ET.fromstring(response.content)
        channel = root.find("channel")
        if channel is None:
            return jobs

        ns = {
            "job": "https://jobicy.com",
            "media": "http://search.yahoo.com/mrss/",
        }

        for item in channel.findall("item"):
            title = (item.findtext("title") or "").strip()
            link = (item.findtext("link") or "").strip()
            date = (item.findtext("pubDate") or "").strip()
            description = (item.findtext("description") or "")

            # Try to get location and salary from job-specific namespaced fields
            location = (
                item.findtext("job:jobLocation", namespaces=ns)
                or item.findtext("jobLocation")
                or "Remote"
            ).strip()

            salary = (
                item.findtext("job:salary", namespaces=ns)
                or item.findtext("salary")
                or ""
            ).strip()

            company = (
                item.findtext("job:hiringOrganization", namespaces=ns)
                or item.findtext("hiringOrganization")
                or ""
            ).strip()

            combined_text = f"{title} {description}"
            if not _contains_keyword(combined_text):
                continue
            if not _is_remote_or_peru(location):
                continue
            if not _salary_is_acceptable(salary, location):
                continue

            jobs.append({
                "title": title,
                "company": company,
                "location": location,
                "salary": salary,
                "link": link,
                "date": date,
                "source": "Jobicy",
            })
    except Exception as exc:
        print(f"[WARNING] Jobicy RSS feed failed: {exc}", file=sys.stderr)
    return jobs


# ---------------------------------------------------------------------------
# Report generation
# ---------------------------------------------------------------------------

_HTML_STYLE = """
<style>
  body { font-family: Arial, sans-serif; background: #f4f4f4; padding: 24px; }
  h1 { color: #2c3e50; }
  .summary { color: #555; margin-bottom: 20px; }
  table { border-collapse: collapse; width: 100%; background: #fff; }
  th { background: #2c3e50; color: #fff; padding: 10px 14px; text-align: left; }
  td { padding: 10px 14px; border-bottom: 1px solid #ddd; vertical-align: top; }
  tr:hover td { background: #f0f8ff; }
  a { color: #2980b9; text-decoration: none; }
  a:hover { text-decoration: underline; }
  .badge { display: inline-block; padding: 2px 8px; border-radius: 10px;
           font-size: 12px; background: #e8f5e9; color: #2e7d32; }
  .no-jobs { color: #888; font-style: italic; }
</style>
"""


def _build_html_report(jobs: list[dict], report_date: str) -> str:
    """Build the full HTML body for the email."""
    if not jobs:
        body = f"""
        <p class="no-jobs">
          No se encontraron vacantes nuevas hoy que cumplan con los criterios de búsqueda.
        </p>
        <p>Criterios aplicados:</p>
        <ul>
          <li>Keywords: Android, Kotlin, Jetpack Compose, Mobile Developer, Android Engineer</li>
          <li>Perú: salario &gt; S/ {MIN_SALARY_PEN:,} soles</li>
          <li>Internacional/Remoto: salario &gt; ${MIN_SALARY_USD:,} USD/mes</li>
          <li>Modalidad: Remoto o Perú</li>
        </ul>
        """
    else:
        rows = ""
        for job in jobs:
            salary_display = job["salary"] if job["salary"] else "No especificado"
            title_cell = (
                f'<a href="{job["link"]}" target="_blank">{job["title"]}</a>'
                if job["link"]
                else job["title"]
            )
            rows += f"""
            <tr>
              <td>{title_cell}</td>
              <td>{job["company"]}</td>
              <td>{job["location"]}</td>
              <td>{salary_display}</td>
              <td>{job["date"]}</td>
              <td><span class="badge">{job["source"]}</span></td>
            </tr>"""

        body = f"""
        <p class="summary">Se encontraron <strong>{len(jobs)}</strong> vacante(s) para tu perfil.</p>
        <table>
          <thead>
            <tr>
              <th>Vacante</th>
              <th>Empresa</th>
              <th>Ubicación</th>
              <th>Salario</th>
              <th>Fecha</th>
              <th>Fuente</th>
            </tr>
          </thead>
          <tbody>{rows}
          </tbody>
        </table>
        """

    return f"""<!DOCTYPE html>
<html lang="es">
<head><meta charset="utf-8">{_HTML_STYLE}</head>
<body>
  <h1>🤖 Reporte diario de vacantes Android Senior</h1>
  <p class="summary">Fecha: {report_date}</p>
  {body}
  <hr>
  <p style="color:#aaa;font-size:12px;">
    Fuentes consultadas: Arbeitnow, Remotive, Jobicy<br>
    Perfil buscado: Senior Android Developer · Kotlin · Jetpack Compose · Clean Architecture
  </p>
</body>
</html>"""


def _build_plain_report(jobs: list[dict], report_date: str) -> str:
    """Build a plain-text fallback body."""
    lines = [
        f"Reporte diario de vacantes Android Senior — {report_date}",
        "=" * 60,
    ]
    if not jobs:
        lines.append("No se encontraron vacantes nuevas hoy.")
    else:
        lines.append(f"Se encontraron {len(jobs)} vacante(s):\n")
        for i, job in enumerate(jobs, 1):
            salary = job["salary"] if job["salary"] else "No especificado"
            lines += [
                f"{i}. {job['title']}",
                f"   Empresa:   {job['company']}",
                f"   Ubicación: {job['location']}",
                f"   Salario:   {salary}",
                f"   Fecha:     {job['date']}",
                f"   Fuente:    {job['source']}",
                f"   Link:      {job['link']}",
                "",
            ]
    return "\n".join(lines)


# ---------------------------------------------------------------------------
# Email sending
# ---------------------------------------------------------------------------


def send_email(jobs: list[dict], report_date: str) -> None:
    """Send the job report email via Gmail SMTP."""
    gmail_user = os.environ.get("GMAIL_USER", "").strip()
    gmail_password = os.environ.get("GMAIL_APP_PASSWORD", "").strip()

    if not gmail_user or not gmail_password:
        print(
            "[ERROR] GMAIL_USER and GMAIL_APP_PASSWORD environment variables must be set.",
            file=sys.stderr,
        )
        sys.exit(1)

    subject = f"🤖 Reporte diario de vacantes Android Senior - {report_date}"

    html_body = _build_html_report(jobs, report_date)
    plain_body = _build_plain_report(jobs, report_date)

    msg = MIMEMultipart("alternative")
    msg["Subject"] = subject
    msg["From"] = gmail_user
    msg["To"] = RECIPIENT_EMAIL

    msg.attach(MIMEText(plain_body, "plain", "utf-8"))
    msg.attach(MIMEText(html_body, "html", "utf-8"))

    try:
        with smtplib.SMTP_SSL("smtp.gmail.com", 465) as server:
            server.login(gmail_user, gmail_password)
            server.sendmail(gmail_user, RECIPIENT_EMAIL, msg.as_string())
        print(f"[OK] Email enviado a {RECIPIENT_EMAIL} — {len(jobs)} vacante(s) encontrada(s).")
    except smtplib.SMTPAuthenticationError:
        print(
            "[ERROR] Autenticación SMTP fallida. Verifica GMAIL_USER y GMAIL_APP_PASSWORD.",
            file=sys.stderr,
        )
        sys.exit(1)
    except Exception as exc:
        print(f"[ERROR] No se pudo enviar el email: {exc}", file=sys.stderr)
        sys.exit(1)


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------


def main() -> None:
    print("[INFO] Iniciando búsqueda de vacantes…")

    report_date = datetime.now(timezone.utc).strftime("%Y-%m-%d")

    all_jobs: list[dict] = []

    print("[INFO] Consultando Arbeitnow…")
    all_jobs.extend(_fetch_arbeitnow())

    print("[INFO] Consultando Remotive…")
    all_jobs.extend(_fetch_remotive())

    print("[INFO] Consultando Jobicy RSS…")
    all_jobs.extend(_fetch_jobicy())

    # De-duplicate by link
    seen: set[str] = set()
    unique_jobs: list[dict] = []
    for job in all_jobs:
        key = job["link"] or f"{job['title']}|{job['company']}"
        if key not in seen:
            seen.add(key)
            unique_jobs.append(job)

    print(f"[INFO] Total de vacantes encontradas (sin duplicados): {len(unique_jobs)}")
    send_email(unique_jobs, report_date)


if __name__ == "__main__":
    main()
