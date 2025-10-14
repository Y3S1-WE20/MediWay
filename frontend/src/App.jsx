import { useState } from 'react'
import HeroSection from './components/HeroSection'
import FeaturesSection from './components/FeaturesSection'
import AboutSection from './components/AboutSection'
import Footer from './components/Footer'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    
    <div className="min-h-screen bg-background">
      <HeroSection />
      <FeaturesSection />
      <AboutSection />
      <Footer />
    </div>
    
  )
}

export default App
