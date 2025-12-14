import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API = "http://localhost:8080/api/v1";

function App() {
  const [services, setServices] = useState([]);
  const [stylists, setStylists] = useState([]);
  const [slots, setSlots] = useState([]);
  
  const [form, setForm] = useState({
    serviceId: "", stylistId: "", date: "", time: "", name: "", email: ""
  });
  
  const [msg, setMsg] = useState("");
  const [booking, setBooking] = useState(false);

  // It runs ONCE when the app first loads
  useEffect(() => {
    axios.get(`${API}/services`).then(res => setServices(res.data));
    axios.get(`${API}/stylists`).then(res => setStylists(res.data));
  }, []);

  // Fetch Slots
  useEffect(() => {
    if (form.serviceId && form.stylistId && form.date) {
      setSlots([]); 
      axios.get(`${API}/slots`, {
        params: {
          stylistId: form.stylistId,
          serviceId: form.serviceId,
          date: form.date
        }
      }).then(res => setSlots(res.data))
        .catch(err => console.error(err));
    }
  }, [form.serviceId, form.stylistId, form.date]);

  // helper to (re)load slots
  const fetchSlots = () => {
    if (form.serviceId && form.stylistId && form.date) {
      axios.get(`${API}/slots`, {
        params: {
          stylistId: form.stylistId,
          serviceId: form.serviceId,
          date: form.date
        }
      }).then(res => setSlots(res.data))
        .catch(err => console.error(err));
    }
  }

  const book = async (e) => {
    e.preventDefault();
    setMsg("Processing...");
    setBooking(true);
    // Normalize time to HH:mm:ss — avoid sending e.g. "09:00:00:00"
    const rawTime = form.time || "";
    let timePart = rawTime;
    if (/^\d{2}:\d{2}$/.test(rawTime)) {
      timePart = rawTime + ":00"; // add seconds
    } else if (/^\d{2}:\d{2}:\d{2}$/.test(rawTime)) {
      timePart = rawTime; // already has seconds
    } else {
      // fallback: try to keep first 8 chars or default to 00:00:00
      timePart = rawTime.slice(0, 8) || "00:00:00";
    }
    const start = `${form.date}T${timePart}`;
    try {
      await axios.post(`${API}/book`, {
        serviceId: form.serviceId,
        stylistId: form.stylistId,
        startTime: start,
        clientName: form.name,
        clientEmail: form.email
      });
      setMsg("✅ Booked Successfully!");
      setForm({...form, time: ""});
      // refresh slots to reflect newly-booked slot
      fetchSlots();
    } catch (err) {
      setMsg("❌ Error: " + (err.response?.data?.error || err.response?.data || err.message));
    }
    setBooking(false);
  };

  const handleChange = (e) => {
    setForm({...form, [e.target.name]: e.target.value});
    if (e.target.name !== "name" && e.target.name !== "email") {
      setForm(prev => ({...prev, [e.target.name]: e.target.value, time: ""}));
    }
  }

  return (
    <div className="container">
      <h1>Urban Cave</h1>
      <form onSubmit={book}>
        <div className="row">
          <select name="serviceId" onChange={handleChange} required>
            <option value="">Select Service</option>
            {services.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
          <select name="stylistId" onChange={handleChange} required>
            <option value="">Select Stylist</option>
            {stylists.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </div>

        <p>Select Date:</p>
        <input type="date" name="date" onChange={handleChange} required />

        <div className="slots-area">
          <p>Available Times:</p>
          <div className="slots-grid">
            {slots.length === 0 && <span className="no-slots">No slots available (Stylist OFF)</span>}
            {slots.map(slot => (
              <button 
                key={slot} 
                type="button" 
                className={form.time === slot ? "slot selected" : "slot"}
                onClick={() => { if (!booking) setForm(prev => ({...prev, time: prev.time === slot ? "" : slot})) }}
                disabled={booking || (form.time && form.time !== slot)}
              >
                {slot}
              </button>
            ))}
          </div>
        </div>

        <input placeholder="Name" name="name" onChange={handleChange} required />
        <input placeholder="Email" name="email" onChange={handleChange} required />

        <button type="submit" className="book-btn" disabled={!form.time || booking}>Confirm</button>
      </form>
      <p>{msg}</p>
    </div>
  );
}
export default App;